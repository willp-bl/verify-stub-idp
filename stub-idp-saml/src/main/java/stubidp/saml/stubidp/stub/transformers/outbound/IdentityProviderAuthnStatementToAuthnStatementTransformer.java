package stubidp.saml.stubidp.stub.transformers.outbound;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnStatement;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;
import stubidp.saml.utils.core.domain.IdentityProviderAuthnStatement;

public class IdentityProviderAuthnStatementToAuthnStatementTransformer {

    @Inject
    public IdentityProviderAuthnStatementToAuthnStatementTransformer(OpenSamlXmlObjectFactory openSamlXmlObjectFactory) {
        this.openSamlXmlObjectFactory = openSamlXmlObjectFactory;
    }

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory;

    public AuthnStatement transform(IdentityProviderAuthnStatement idaAuthnStatement) {
        AuthnStatement authnStatement = openSamlXmlObjectFactory.createAuthnStatement();
        AuthnContext authnContext = openSamlXmlObjectFactory.createAuthnContext();
        authnContext.setAuthnContextClassRef(openSamlXmlObjectFactory.createAuthnContextClassReference(idaAuthnStatement.getAuthnContext().getUri()));
        authnStatement.setAuthnContext(authnContext);
        authnStatement.setAuthnInstant(DateTime.now());
        return authnStatement;
    }

}
