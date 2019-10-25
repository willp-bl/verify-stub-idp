package stubidp.saml.hub.hub.validators.response.common;

import com.google.common.base.Strings;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import stubidp.saml.hub.core.errors.SamlTransformationErrorFactory;
import stubidp.saml.hub.hub.exception.SamlValidationException;

public class IssuerValidator {
    public static void validate(Response response) {
        Issuer issuer = response.getIssuer();
        if (issuer == null) throw new SamlValidationException(SamlTransformationErrorFactory.missingIssuer());

        String issuerId = issuer.getValue();
        if (Strings.isNullOrEmpty(issuerId)) throw new SamlValidationException(SamlTransformationErrorFactory.emptyIssuer());

        String issuerFormat = issuer.getFormat();
        if (issuerFormat != null && !NameIDType.ENTITY.equals(issuerFormat))
            throw new SamlValidationException(SamlTransformationErrorFactory.illegalIssuerFormat(issuerFormat, NameIDType.ENTITY));
    }
}
