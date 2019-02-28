package stubidp.saml.hub.hub.validators.authnrequest;

import com.google.inject.Inject;
import io.dropwizard.util.Duration;
import org.joda.time.DateTime;
import stubidp.saml.hub.hub.configuration.SamlAuthnRequestValidityDurationConfiguration;

public class AuthnRequestIssueInstantValidator {
    private final SamlAuthnRequestValidityDurationConfiguration samlAuthnRequestValidityDurationConfiguration;

    @Inject
    public AuthnRequestIssueInstantValidator(SamlAuthnRequestValidityDurationConfiguration samlAuthnRequestValidityDurationConfiguration) {

        this.samlAuthnRequestValidityDurationConfiguration = samlAuthnRequestValidityDurationConfiguration;
    }

    public boolean isValid(DateTime issueInstant) {
        final Duration authnRequestValidityDuration = samlAuthnRequestValidityDurationConfiguration.getAuthnRequestValidityDuration();
        return !issueInstant.isBefore(DateTime.now().minus(authnRequestValidityDuration.toMilliseconds()));
    }
}
