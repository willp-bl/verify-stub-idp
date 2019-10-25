package stubidp.saml.utils.core.test.builders;

import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import stubidp.saml.extensions.extensions.IdaAuthnContext;
import stubidp.saml.utils.core.test.OpenSamlXmlObjectFactory;

public class AuthnContextClassRefBuilder {

    private String value = IdaAuthnContext.LEVEL_2_AUTHN_CTX;

    public static AuthnContextClassRefBuilder anAuthnContextClassRef() {
        return new AuthnContextClassRefBuilder();
    }

    public AuthnContextClassRefBuilder withAuthnContextClasRefValue(String value) {
        this.value = value;
        return this;
    }

    public AuthnContextClassRef build() {
        return new OpenSamlXmlObjectFactory().createAuthnContextClassReference(value);
    }

}
