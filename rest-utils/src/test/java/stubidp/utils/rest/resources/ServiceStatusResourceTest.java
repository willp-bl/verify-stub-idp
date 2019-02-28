package stubidp.utils.rest.resources;

import org.junit.After;
import org.junit.Test;
import stubidp.utils.rest.configuration.ServiceStatus;
import stubidp.utils.rest.resources.ServiceStatusResource;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceStatusResourceTest {

    ServiceStatus instance  = ServiceStatus.getInstance();

    @Test
    public void shouldReturn200WhenInitialised() throws Exception {
        ServiceStatusResource serviceStatusResource = new ServiceStatusResource();
        assertThat(serviceStatusResource.isOnline().getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldReturn503WhenServiceStatusIsFalse() throws Exception {
        ServiceStatusResource serviceStatusResource = new ServiceStatusResource();
        instance.setServiceStatus(false);
        assertThat(serviceStatusResource.isOnline().getStatus()).isEqualTo(503);
    }

    @After
    public void tearDown(){
        instance.setServiceStatus(true);
    }
}
