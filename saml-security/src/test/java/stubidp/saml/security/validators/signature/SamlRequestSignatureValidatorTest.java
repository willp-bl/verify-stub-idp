package stubidp.saml.security.validators.signature;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import stubidp.saml.extensions.validation.SamlTransformationErrorException;
import stubidp.saml.extensions.validation.SamlValidationResponse;
import stubidp.saml.security.SamlMessageSignatureValidator;
import stubidp.saml.security.saml.OpenSAMLMockitoRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static stubidp.saml.security.errors.SamlTransformationErrorFactory.unableToDecrypt;

@RunWith(OpenSAMLMockitoRunner.class)
public class SamlRequestSignatureValidatorTest {

    private SamlMessageSignatureValidator samlMessageSignatureValidator;
    private SamlRequestSignatureValidator<AuthnRequest> requestSignatureValidator;
    private AuthnRequest authnRequest;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        samlMessageSignatureValidator = mock(SamlMessageSignatureValidator.class);
        requestSignatureValidator = new SamlRequestSignatureValidator<>(samlMessageSignatureValidator);
        authnRequest = mock(AuthnRequest.class);
    }

    @Test
    public void validate_shouldDoNothingIfAuthnRequestSignatureIsValid() {
        when(samlMessageSignatureValidator.validate(authnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME)).thenReturn(SamlValidationResponse.aValidResponse());

        requestSignatureValidator.validate(authnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        verify(samlMessageSignatureValidator).validate(authnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    @Test
    public void validate_shouldThrowExceptionIfAuthnRequestSignatureIsInvalid() {
        expectedException.expect(SamlTransformationErrorException.class);
        expectedException.expectMessage("Error");

        SamlValidationResponse invalidResponse = SamlValidationResponse.anInvalidResponse(unableToDecrypt("Error"));
        when(samlMessageSignatureValidator.validate(authnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME)).thenReturn(invalidResponse);

        requestSignatureValidator.validate(authnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }
}
