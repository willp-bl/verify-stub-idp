package stubidp.saml.hub.hub.configuration;

import io.dropwizard.util.Duration;

public interface SamlDuplicateRequestValidationConfiguration {
    Duration getAuthnRequestIdExpirationDuration();
}
