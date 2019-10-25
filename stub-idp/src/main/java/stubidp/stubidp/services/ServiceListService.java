package stubidp.stubidp.services;

import org.apache.log4j.Logger;
import stubidp.stubidp.configuration.SingleIdpConfiguration;
import stubidp.stubidp.domain.Service;
import stubidp.stubidp.exceptions.FeatureNotEnabledException;
import stubidp.utils.rest.jerseyclient.JsonClient;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ServiceListService {

    private final Logger LOG = Logger.getLogger(ServiceListService.class);

    private final SingleIdpConfiguration singleIdpConfiguration;
    private final JsonClient jsonClient;

    @Inject
    public ServiceListService(SingleIdpConfiguration singleIdpConfiguration, JsonClient jsonClient) {
        this.singleIdpConfiguration = singleIdpConfiguration;
        this.jsonClient = jsonClient;
    }

    public List<Service> getServices() throws FeatureNotEnabledException {

        if (!singleIdpConfiguration.isEnabled()) throw new FeatureNotEnabledException();

        return readListFromHub();
    }

    private List<Service> readListFromHub(){
        try {
            List<Service> services = jsonClient.get(singleIdpConfiguration.getServiceListUri(), new GenericType<List<Service>>() {});

            return services;
        } catch (RuntimeException ex) {
            LOG.error(MessageFormat.format("Error getting service list from {0}", singleIdpConfiguration.getServiceListUri().toString()), ex);
        }
        return new ArrayList<Service>() {};
    }
}
