package stubidp.saml.utils.core.test.builders;

import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import stubidp.saml.utils.core.test.OpenSamlXmlObjectFactory;

public class AuthnContextBuilder {

    private static OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private AuthnContextClassRef authnContextClassRef = AuthnContextClassRefBuilder.anAuthnContextClassRef().build();

    public static AuthnContextBuilder anAuthnContext() {
        return new AuthnContextBuilder();
    }

    public AuthnContext build() {
        AuthnContext authnContext = openSamlXmlObjectFactory.createAuthnContext();
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        return authnContext;
    }

    public AuthnContextBuilder withAuthnContextClassRef(AuthnContextClassRef authnContextClassRef) {
        this.authnContextClassRef = authnContextClassRef;
        return this;
    }
}
