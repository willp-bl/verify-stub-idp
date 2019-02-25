package uk.gov.ida.saml.core.validators.assertion;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import stubidp.saml.utils.core.domain.AuthnContext;
import stubidp.saml.utils.core.test.OpenSAMLMockitoRunner;
import stubidp.saml.utils.core.test.SamlTransformationErrorManagerTestHelper;
import stubidp.saml.utils.core.test.builders.AttributeStatementBuilder;
import uk.gov.ida.saml.core.test.builders.IdpFraudEventIdAttributeBuilder;
import stubidp.saml.utils.core.test.builders.SubjectBuilder;
import uk.gov.ida.saml.core.errors.SamlTransformationErrorFactory;import uk.gov.ida.saml.core.validators.subject.AssertionSubjectValidator;
import uk.gov.ida.saml.core.validators.subjectconfirmation.AssertionSubjectConfirmationValidator;
import stubidp.saml.security.validators.issuer.IssuerValidator;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static stubidp.saml.utils.core.test.builders.AssertionBuilder.anAssertion;
import static stubidp.saml.utils.core.test.builders.AuthnContextBuilder.anAuthnContext;
import static stubidp.saml.utils.core.test.builders.AuthnContextClassRefBuilder.anAuthnContextClassRef;
import static stubidp.saml.utils.core.test.builders.SubjectConfirmationBuilder.aSubjectConfirmation;
import static stubidp.saml.utils.core.test.builders.AuthnStatementBuilder.anAuthnStatement;

@RunWith(OpenSAMLMockitoRunner.class)
public class IdentityProviderAssertionValidatorTest {

    @Mock
    private AssertionSubjectValidator subjectValidator;
    @Mock
    private IssuerValidator issuerValidator;
    @Mock
    private AssertionSubjectConfirmationValidator subjectConfirmationValidator;
    @Mock
    private AssertionAttributeStatementValidator assertionAttributeStatementValidator;

    @Test
    public void validate_shouldDelegateSubjectConfirmationValidation() throws Exception {
        String requestId = UUID.randomUUID().toString();
        String expectedRecipientId = UUID.randomUUID().toString();
        final SubjectConfirmation subjectConfirmation = aSubjectConfirmation().build();
        Assertion assertion = anAssertion()
                .withSubject(SubjectBuilder.aSubject().withSubjectConfirmation(subjectConfirmation).build())
                .buildUnencrypted();

        IdentityProviderAssertionValidator validator = new IdentityProviderAssertionValidator(issuerValidator, subjectValidator, assertionAttributeStatementValidator, subjectConfirmationValidator);
        validator.validate(assertion, requestId, expectedRecipientId);

        verify(subjectConfirmationValidator).validate(subjectConfirmation, requestId, expectedRecipientId);
    }

    @Test
    public void validate_shouldThrowExceptionIfNoSubjectConfirmationMethodAttributeHasBearerValue() throws Exception {
        String someID = UUID.randomUUID().toString();
        final Assertion assertion = anAssertion().withId(someID).addAuthnStatement(anAuthnStatement().build()).withSubject(SubjectBuilder.aSubject().withSubjectConfirmation(aSubjectConfirmation().withMethod("invalid").build()).build()).buildUnencrypted();
        final IdentityProviderAssertionValidator validator = new IdentityProviderAssertionValidator(issuerValidator, subjectValidator, assertionAttributeStatementValidator, subjectConfirmationValidator);

        SamlTransformationErrorManagerTestHelper.validateFail(
                () -> validator.validateSubject(assertion, "", ""),
                SamlTransformationErrorFactory.noSubjectConfirmationWithBearerMethod(assertion.getID())
        );
    }

    @Test
    public void validate_shouldThrowExceptionIfInvalidFraudEventTypeUsed(){
        final AuthnContextClassRef authnContextClassRef = anAuthnContextClassRef().withAuthnContextClasRefValue(AuthnContext.LEVEL_X.getUri()).build();
        final org.opensaml.saml.saml2.core.AuthnContext authnContext = anAuthnContext().withAuthnContextClassRef(authnContextClassRef).build();

        String someID = UUID.randomUUID().toString();
        final Assertion assertion =
                anAssertion().withId(someID)
                .addAttributeStatement(AttributeStatementBuilder.anAttributeStatement().addAttribute(IdpFraudEventIdAttributeBuilder.anIdpFraudEventIdAttribute().buildInvalidAttribute()).build())
                .addAuthnStatement(anAuthnStatement().withAuthnContext(authnContext).build())
                .buildUnencrypted();
        final IdentityProviderAssertionValidator validator = new IdentityProviderAssertionValidator(issuerValidator, subjectValidator, assertionAttributeStatementValidator, subjectConfirmationValidator);
        validator.validateSubject(assertion, someID, UUID.randomUUID().toString());
        verify(assertionAttributeStatementValidator).validateFraudEvent(assertion);
    }
}
