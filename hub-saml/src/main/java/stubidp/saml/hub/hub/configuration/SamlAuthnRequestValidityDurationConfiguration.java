package stubidp.saml.hub.hub.configuration;

import io.dropwizard.util.Duration;

public interface SamlAuthnRequestValidityDurationConfiguration {
    Duration getAuthnRequestValidityDuration();
}
