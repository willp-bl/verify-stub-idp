package stubidp.stubidp.utils;

import stubidp.stubidp.configuration.IdpStubsConfiguration;
import stubidp.stubidp.configuration.StubIdp;

import java.util.Collection;

public class TestIdpStubsConfiguration extends IdpStubsConfiguration {
    public TestIdpStubsConfiguration(Collection<StubIdp> stubIdps) {
        this.stubIdps = stubIdps;
    }
}
