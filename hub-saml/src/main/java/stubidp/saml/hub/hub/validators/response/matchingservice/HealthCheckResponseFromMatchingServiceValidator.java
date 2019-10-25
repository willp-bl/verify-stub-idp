package stubidp.saml.hub.hub.validators.response.matchingservice;

import com.google.common.base.Strings;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.Signature;
import stubidp.saml.extensions.domain.SamlStatusCode;
import stubidp.saml.hub.core.errors.SamlTransformationErrorFactory;
import stubidp.saml.hub.hub.exception.SamlValidationException;
import stubidp.saml.hub.hub.validators.response.common.IssuerValidator;
import stubidp.saml.hub.hub.validators.response.common.RequestIdValidator;

import static stubidp.saml.security.validators.signature.SamlSignatureUtil.isSignaturePresent;

public class HealthCheckResponseFromMatchingServiceValidator {

    public void validate(Response response) {
        IssuerValidator.validate(response);
        RequestIdValidator.validate(response);
        validateResponse(response);
    }

    private void validateResponse(Response response) {
        if (Strings.isNullOrEmpty(response.getID())) throw new SamlValidationException(SamlTransformationErrorFactory.missingId());

        Signature signature = response.getSignature();
        if (signature == null) throw new SamlValidationException(SamlTransformationErrorFactory.missingSignature());
        if (!isSignaturePresent(signature)) throw new SamlValidationException(SamlTransformationErrorFactory.signatureNotSigned());

        validateStatusAndSubStatus(response);
    }

    protected void validateStatusAndSubStatus(Response response) {
        StatusCode statusCode = response.getStatus().getStatusCode();

        if(StatusCode.REQUESTER.equals(statusCode.getValue())) return;

        if (statusCode.getStatusCode() == null) throw new SamlValidationException(SamlTransformationErrorFactory.missingSubStatus());

        String statusCodeValue = statusCode.getValue();
        if (!StatusCode.SUCCESS.equals(statusCodeValue)) throw new SamlValidationException(SamlTransformationErrorFactory.invalidStatusCode(statusCodeValue));

        String subStatusCodeValue = statusCode.getStatusCode().getValue();
        if (!SamlStatusCode.HEALTHY.equals(subStatusCodeValue)) {
            throw new SamlValidationException(SamlTransformationErrorFactory.invalidSubStatusCode(subStatusCodeValue, StatusCode.SUCCESS));
        }
    }
}
