package stubidp.saml.utils.hub.transformers.outbound;

import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;
import stubidp.saml.utils.core.domain.DetailedStatusCode;
import stubidp.saml.utils.core.domain.MatchingServiceIdaStatus;
import stubidp.saml.utils.core.transformers.outbound.IdaStatusMarshaller;

import javax.inject.Inject;
import java.util.AbstractMap;
import java.util.Map;

public class MatchingServiceIdaStatusMarshaller extends IdaStatusMarshaller<MatchingServiceIdaStatus> {

    private static final Map<MatchingServiceIdaStatus, DetailedStatusCode> REST_TO_SAML_CODES =
            Map.<MatchingServiceIdaStatus, DetailedStatusCode>ofEntries(
                    Map.entry(MatchingServiceIdaStatus.MatchingServiceMatch, DetailedStatusCode.MatchingServiceMatch),
                    Map.entry(MatchingServiceIdaStatus.NoMatchingServiceMatchFromMatchingService, DetailedStatusCode.NoMatchingServiceMatchFromMatchingService),
                    Map.entry(MatchingServiceIdaStatus.RequesterError, DetailedStatusCode.RequesterErrorFromIdp),
                    Map.entry(MatchingServiceIdaStatus.Healthy, DetailedStatusCode.Healthy)
            );

    @Inject
    public MatchingServiceIdaStatusMarshaller(OpenSamlXmlObjectFactory samlObjectFactory) {
        super(samlObjectFactory);
    }

    @Override
    protected DetailedStatusCode getDetailedStatusCode(MatchingServiceIdaStatus originalStatus) {
        return REST_TO_SAML_CODES.get(originalStatus);
    }
}
