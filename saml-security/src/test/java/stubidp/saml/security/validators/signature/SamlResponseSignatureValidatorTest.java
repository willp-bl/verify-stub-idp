package stubidp.saml.security.validators.signature;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import stubidp.saml.extensions.validation.SamlTransformationErrorException;
import stubidp.saml.extensions.validation.SamlValidationResponse;
import stubidp.saml.security.SamlMessageSignatureValidator;
import stubidp.saml.security.saml.OpenSAMLMockitoRunner;
import stubidp.saml.security.validators.ValidatedResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static stubidp.saml.security.errors.SamlTransformationErrorFactory.unableToDecrypt;

@RunWith(OpenSAMLMockitoRunner.class)
public class SamlResponseSignatureValidatorTest {

    private static final String RESPONSE_ID = "RESPONSEID";
    private SamlMessageSignatureValidator samlMessageSignatureValidator;
    private SamlResponseSignatureValidator responseSignatureValidator;
    private Response response;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        samlMessageSignatureValidator = mock(SamlMessageSignatureValidator.class);
        responseSignatureValidator = new SamlResponseSignatureValidator(samlMessageSignatureValidator);
        response = mock(Response.class);
        when(response.getID()).thenReturn(RESPONSE_ID);
    }

    @Test
    public void validate_shouldDoNothingIfResponseSignatureIsValid() {
        when(samlMessageSignatureValidator.validate(response, SPSSODescriptor.DEFAULT_ELEMENT_NAME)).thenReturn(SamlValidationResponse.aValidResponse());

        ValidatedResponse validatedResponse = responseSignatureValidator.validate(response, SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        verify(samlMessageSignatureValidator).validate(response, SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        assertEquals(validatedResponse.getID(), RESPONSE_ID);
    }

    @Test
    public void validate_shouldThrowExceptionIfResponseSignatureIsInvalid() {
        expectedException.expect(SamlTransformationErrorException.class);
        expectedException.expectMessage("Error");

        SamlValidationResponse invalidResponse = SamlValidationResponse.anInvalidResponse(unableToDecrypt("Error"));
        when(samlMessageSignatureValidator.validate(response, SPSSODescriptor.DEFAULT_ELEMENT_NAME)).thenReturn(invalidResponse);

        responseSignatureValidator.validate(response, SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }
}