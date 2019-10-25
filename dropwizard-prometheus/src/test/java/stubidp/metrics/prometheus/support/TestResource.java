package stubidp.metrics.prometheus.support;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static stubidp.metrics.prometheus.support.TestResource.TEST_RESOURCE_PATH;

@Path(TEST_RESOURCE_PATH)
public class TestResource {

    public static final String TEST_RESOURCE_PATH = "/";

    @Timed
    @GET
    public String get() {
        return "hello";
    }
}
