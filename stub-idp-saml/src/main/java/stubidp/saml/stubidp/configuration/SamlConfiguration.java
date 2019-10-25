package stubidp.saml.stubidp.configuration;

import java.net.URI;

/*
 * Configuration class required to generate SamlRequests or SamlResponses
 */
public interface SamlConfiguration {
    URI getExpectedDestinationHost();

    String getEntityId();
}
