package stubidp.saml.hub.test.outbound.transformers;

import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnStatement;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;
import stubidp.saml.hub.core.domain.MatchingServiceAuthnStatement;

public class MatchingServiceAuthnStatementToAuthnStatementTransformer {

    @Inject
    public MatchingServiceAuthnStatementToAuthnStatementTransformer(
            final OpenSamlXmlObjectFactory openSamlXmlObjectFactory) {

        this.openSamlXmlObjectFactory = openSamlXmlObjectFactory;
    }

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory;

    public AuthnStatement transform(MatchingServiceAuthnStatement idaAuthnStatement) {
        AuthnStatement authnStatement = openSamlXmlObjectFactory.createAuthnStatement();
        AuthnContext authnContext = openSamlXmlObjectFactory.createAuthnContext();
        authnContext.setAuthnContextClassRef(openSamlXmlObjectFactory.createAuthnContextClassReference(idaAuthnStatement.getAuthnContext().getUri()));
        authnStatement.setAuthnContext(authnContext);
        authnStatement.setAuthnInstant(DateTime.now());
        return authnStatement;
    }
}
