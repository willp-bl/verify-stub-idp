package stubidp.saml.hub.hub.validators.response.matchingservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import stubidp.saml.hub.core.errors.SamlTransformationErrorFactory;
import stubidp.saml.hub.hub.validators.response.matchingservice.ResponseAssertionsFromMatchingServiceValidator;
import stubidp.saml.utils.core.test.OpenSAMLMockitoRunner;
import stubidp.test.devpki.TestEntityIds;
import stubidp.saml.extensions.validation.SamlTransformationErrorException;
import stubidp.saml.extensions.validation.SamlValidationSpecificationFailure;
import stubidp.saml.hub.core.validators.assertion.IdentityProviderAssertionValidator;
import stubidp.saml.security.validators.ValidatedAssertions;
import stubidp.saml.security.validators.ValidatedResponse;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static stubidp.saml.utils.core.test.builders.AssertionBuilder.anAssertion;
import static stubidp.saml.utils.core.test.builders.AuthnStatementBuilder.anAuthnStatement;
import static stubidp.saml.utils.core.test.builders.ResponseBuilder.aResponse;
import static stubidp.saml.utils.core.test.builders.StatusBuilder.aStatus;
import static stubidp.saml.utils.core.test.builders.StatusCodeBuilder.aStatusCode;

@RunWith(OpenSAMLMockitoRunner.class)
public class ResponseAssertionsFromMatchingServiceValidatorTest {

    @Mock
    private IdentityProviderAssertionValidator assertionValidator;

    private ResponseAssertionsFromMatchingServiceValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new ResponseAssertionsFromMatchingServiceValidator(assertionValidator, TestEntityIds.HUB_ENTITY_ID);
    }

    @Test(expected = SamlTransformationErrorException.class)
    public void validate_shouldThrowExceptionIfAssertionDoesNotContainAnAuthnContext() throws Exception {
        String requestId = "some-request-id";
        final Response response = aResponse()
                .withInResponseTo(requestId)
                .build();
        final Assertion assertion = anAssertion().addAuthnStatement(anAuthnStatement().withAuthnContext(null).build()).buildUnencrypted();

        validateThrows(response, assertion, SamlTransformationErrorFactory.authnContextMissingError());
    }

    @Test(expected = SamlTransformationErrorException.class)
    public void validate_shouldThrowExceptionIfAssertionDoesNotContainAnAuthnStatement() throws Exception {
        String requestId = "some-request-id";
        final Response response = aResponse()
                .withInResponseTo(requestId)
                .build();
        validateThrows(response, anAssertion().buildUnencrypted(), SamlTransformationErrorFactory.missingAuthnStatement());
    }

    @Test
    public void validate_shouldNotThrowExceptionIfResponseIsANoMatch() throws Exception {
        String requestId = "some-request-id";
        final Response response = aResponse()
                .withStatus(aStatus().withStatusCode(aStatusCode().withValue(StatusCode.RESPONDER).build()).build())
                .withInResponseTo(requestId)
                .build();

        validator.validate(new ValidatedResponse(response), new ValidatedAssertions(singletonList(anAssertion().buildUnencrypted())));
    }

    private void validateThrows(Response response, Assertion assertion, SamlValidationSpecificationFailure samlValidationSpecificationFailure) {
        try {
            validator.validate(new ValidatedResponse(response), new ValidatedAssertions(singletonList(assertion)));
        } catch (SamlTransformationErrorException e) {
            assertThat(e.getMessage()).isEqualTo(samlValidationSpecificationFailure.getErrorMessage());
            assertThat(e.getLogLevel()).isEqualTo(samlValidationSpecificationFailure.getLogLevel());
            throw e;
        }
    }
}
