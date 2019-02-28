package stubidp.saml.hub.hub.validators.response.idp.components;

import com.google.common.base.Strings;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.Signature;
import stubidp.saml.extensions.validation.SamlValidationSpecificationFailure;
import stubidp.saml.hub.core.errors.SamlTransformationErrorFactory;
import stubidp.saml.hub.hub.transformers.inbound.SamlStatusToAuthenticationStatusCodeMapper;
import stubidp.saml.hub.hub.validators.response.common.IssuerValidator;
import stubidp.saml.hub.hub.validators.response.common.RequestIdValidator;
import stubidp.saml.hub.hub.exception.SamlValidationException;

import java.util.List;
import java.util.Optional;

import static stubidp.saml.security.validators.signature.SamlSignatureUtil.isSignaturePresent;

public class EncryptedResponseFromIdpValidator<T extends Enum> {
    private static final int SUB_STATUS_CODE_LIMIT = 1;
    private SamlStatusToAuthenticationStatusCodeMapper<T> statusCodeMapper;

    public EncryptedResponseFromIdpValidator(final SamlStatusToAuthenticationStatusCodeMapper<T> statusCodeMapper) {
        this.statusCodeMapper = statusCodeMapper;
    }

    protected void validateAssertionPresence(Response response) {
        if (!response.getAssertions().isEmpty()) throw new SamlValidationException(SamlTransformationErrorFactory.unencryptedAssertion());

        boolean responseWasSuccessful = response.getStatus().getStatusCode().getValue().equals(StatusCode.SUCCESS);
        List<EncryptedAssertion> encryptedAssertions = response.getEncryptedAssertions();

        if (responseWasSuccessful && encryptedAssertions.isEmpty()) {
            throw new SamlValidationException(SamlTransformationErrorFactory.missingSuccessUnEncryptedAssertions());
        }

        if (!responseWasSuccessful && !encryptedAssertions.isEmpty()) {
            throw new SamlValidationException(SamlTransformationErrorFactory.nonSuccessHasUnEncryptedAssertions());
        }

        if (responseWasSuccessful && encryptedAssertions.size() != 2) {
            throw new SamlValidationException(SamlTransformationErrorFactory.unexpectedNumberOfAssertions(2, encryptedAssertions.size()));
        }
    }

    public void validate(Response response) {
        IssuerValidator.validate(response);
        RequestIdValidator.validate(response);
        validateResponse(response);
    }

    private void validateResponse(Response response) {
        if (Strings.isNullOrEmpty(response.getID())) throw new SamlValidationException(SamlTransformationErrorFactory.missingId());
        if (response.getIssueInstant() == null) throw new SamlValidationException(SamlTransformationErrorFactory.missingIssueInstant(response.getID()));

        Signature signature = response.getSignature();
        if (signature == null) throw new SamlValidationException(SamlTransformationErrorFactory.missingSignature());
        if (!isSignaturePresent(signature)) throw new SamlValidationException(SamlTransformationErrorFactory.signatureNotSigned());

        validateStatus(response.getStatus());
        validateAssertionPresence(response);
    }

    private void validateStatus(Status status) {
        validateStatusCode(status.getStatusCode(), 0);

        Optional<T> mappedStatus = statusCodeMapper.map(status);
        if (!mappedStatus.isPresent()) fail(status);
    }

    private void fail(Status status) {
        StatusCode statusCode = status.getStatusCode();
        StatusCode subStatusCode = statusCode.getStatusCode();

        if (subStatusCode == null) throw new SamlValidationException(SamlTransformationErrorFactory.invalidStatusCode(statusCode.getValue()));

        SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.invalidSubStatusCode(
                subStatusCode.getValue(),
                statusCode.getValue()
        );
        throw new SamlValidationException(failure);
    }

    private void validateStatusCode(StatusCode statusCode, int subStatusCount) {
        if (subStatusCount > SUB_STATUS_CODE_LIMIT) {
            throw new SamlValidationException(SamlTransformationErrorFactory.nestedSubStatusCodesBreached(SUB_STATUS_CODE_LIMIT));
        }

        StatusCode subStatus = statusCode.getStatusCode();
        if (subStatus != null) validateStatusCode(subStatus, subStatusCount + 1);
    }
}
