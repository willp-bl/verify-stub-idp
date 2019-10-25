package stubidp.saml.hub.core.domain;

import stubidp.saml.utils.core.domain.AuthnContext;

public final class MatchingServiceAuthnStatement {

    private AuthnContext authnContext;

    private MatchingServiceAuthnStatement() {
    }

    private MatchingServiceAuthnStatement(AuthnContext authnContext) {
        this.authnContext = authnContext;
    }

    public AuthnContext getAuthnContext() {
        return authnContext;
    }

    public static MatchingServiceAuthnStatement createIdaAuthnStatement(
            AuthnContext authnContext) {

        return new MatchingServiceAuthnStatement(authnContext);
    }
}
