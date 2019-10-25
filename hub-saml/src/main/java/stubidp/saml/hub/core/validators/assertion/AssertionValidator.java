package stubidp.saml.hub.core.validators.assertion;

import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.xmlsec.signature.Signature;
import stubidp.saml.extensions.validation.SamlTransformationErrorException;
import stubidp.saml.extensions.validation.SamlValidationSpecificationFailure;
import stubidp.saml.hub.core.errors.SamlTransformationErrorFactory;
import stubidp.saml.hub.core.validators.subject.AssertionSubjectValidator;
import stubidp.saml.hub.core.validators.subjectconfirmation.BasicAssertionSubjectConfirmationValidator;
import stubidp.saml.security.validators.issuer.IssuerValidator;
import stubidp.saml.security.validators.signature.SamlSignatureUtil;

public class AssertionValidator {

    private final IssuerValidator issuerValidator;
    private final AssertionSubjectValidator subjectValidator;
    final AssertionAttributeStatementValidator assertionAttributeStatementValidator;
    private final BasicAssertionSubjectConfirmationValidator basicAssertionSubjectConfirmationValidator;
    private final boolean signedAssertions;

    public AssertionValidator(
            IssuerValidator issuerValidator,
            AssertionSubjectValidator subjectValidator,
            AssertionAttributeStatementValidator assertionAttributeStatementValidator,
            BasicAssertionSubjectConfirmationValidator basicAssertionSubjectConfirmationValidator) {
        this(issuerValidator, subjectValidator, assertionAttributeStatementValidator, basicAssertionSubjectConfirmationValidator, true);
    }

    public AssertionValidator(
            IssuerValidator issuerValidator,
            AssertionSubjectValidator subjectValidator,
            AssertionAttributeStatementValidator assertionAttributeStatementValidator,
            BasicAssertionSubjectConfirmationValidator basicAssertionSubjectConfirmationValidator,
            boolean signedAssertions) {

        this.issuerValidator = issuerValidator;
        this.subjectValidator = subjectValidator;
        this.assertionAttributeStatementValidator = assertionAttributeStatementValidator;
        this.basicAssertionSubjectConfirmationValidator = basicAssertionSubjectConfirmationValidator;
        this.signedAssertions = signedAssertions;
    }

    public void validate(
            Assertion assertion,
            String requestId,
            String expectedRecipientId) {

        Signature signature = assertion.getSignature();
        if (assertion.getID() == null) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingId();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
        if(signedAssertions) {
            if (signature == null) {
                SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.assertionSignatureMissing(assertion.getID());
                throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
            }
            if (!SamlSignatureUtil.isSignaturePresent(signature)) {
                SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.assertionNotSigned(assertion.getID());
                throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
            }
        } else {
            if (signature != null) {
                SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.assertionSignaturePresent(assertion.getID());
                throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
            }
        }
        if (assertion.getIssueInstant() == null) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingIssueInstant(assertion.getID());
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
        if (assertion.getVersion() == null) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingVersion(assertion.getID());
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
        if (!assertion.getVersion().equals(SAMLVersion.VERSION_20)) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.illegalVersion(assertion.getID());
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        issuerValidator.validate(assertion.getIssuer());
        assertionAttributeStatementValidator.validate(assertion);

        validateSubject(assertion, requestId, expectedRecipientId);
        basicAssertionSubjectConfirmationValidator.validate(assertion.getSubject().getSubjectConfirmations().get(0));
    }

    protected void validateSubject(
            Assertion assertion,
            String requestId,
            String expectedRecipientId) {

        subjectValidator.validate(assertion.getSubject(), assertion.getID());
    }
}
