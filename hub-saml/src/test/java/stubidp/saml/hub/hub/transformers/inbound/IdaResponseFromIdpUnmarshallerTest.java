package stubidp.saml.hub.hub.transformers.inbound;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.signature.Signature;
import stubidp.saml.hub.core.test.builders.PassthroughAssertionBuilder;
import stubidp.saml.utils.core.domain.PassthroughAssertion;
import stubidp.saml.utils.core.test.OpenSAMLMockitoRunner;
import stubidp.saml.utils.core.test.builders.SignatureBuilder;
import stubidp.saml.hub.hub.domain.InboundResponseFromIdp;
import stubidp.saml.security.validators.ValidatedAssertions;
import stubidp.saml.security.validators.ValidatedResponse;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static stubidp.saml.utils.core.test.builders.AssertionBuilder.anAssertion;
import static stubidp.saml.utils.core.test.builders.AttributeStatementBuilder.anAttributeStatement;
import static stubidp.saml.utils.core.test.builders.AuthnStatementBuilder.anAuthnStatement;

@RunWith(OpenSAMLMockitoRunner.class)
public class IdaResponseFromIdpUnmarshallerTest {
    @Mock
    private IdpIdaStatusUnmarshaller statusUnmarshaller;
    @Mock
    private PassthroughAssertionUnmarshaller passthroughAssertionUnmarshaller;
    @Mock
    private Response response;
    @Mock
    private Issuer issuer = null;
    private IdaResponseFromIdpUnmarshaller unmarshaller;
    private Signature signature = new SignatureBuilder().build();

    @Before
    public void setup() {
        when(response.getIssuer()).thenReturn(issuer);
        when(response.getDestination()).thenReturn("http://hello.com");
        when(response.getSignature()).thenReturn(signature);
        unmarshaller = new IdaResponseFromIdpUnmarshaller(statusUnmarshaller, passthroughAssertionUnmarshaller);
    }

    @Test
    public void transform_shouldTransformTheSamlResponseToIdaResponseByIdp() throws Exception {
        Assertion mdsAssertion = anAssertion().addAttributeStatement(anAttributeStatement().build()).buildUnencrypted();
        Assertion authnStatementAssertion = anAssertion().addAuthnStatement(anAuthnStatement().build()).buildUnencrypted();

        when(response.getAssertions()).thenReturn(newArrayList(mdsAssertion, authnStatementAssertion));
        PassthroughAssertion passthroughMdsAssertion = PassthroughAssertionBuilder.aPassthroughAssertion().buildMatchingDatasetAssertion();
        when(passthroughAssertionUnmarshaller.fromAssertion(mdsAssertion)).thenReturn(passthroughMdsAssertion);
        PassthroughAssertion passthroughAuthnAssertion = PassthroughAssertionBuilder.aPassthroughAssertion().buildAuthnStatementAssertion();
        when(passthroughAssertionUnmarshaller.fromAssertion(authnStatementAssertion)).thenReturn(passthroughAuthnAssertion);

        InboundResponseFromIdp inboundResponseFromIdp = unmarshaller.fromSaml(new ValidatedResponse(response), new ValidatedAssertions(response.getAssertions()));

        assertThat(inboundResponseFromIdp.getSignature().isPresent()).isEqualTo(true);
        assertThat(inboundResponseFromIdp.getMatchingDatasetAssertion().isPresent()).isEqualTo(true);
        assertThat(inboundResponseFromIdp.getAuthnStatementAssertion().isPresent()).isEqualTo(true);
        assertThat(inboundResponseFromIdp.getSignature().get()).isEqualTo(signature);
        assertThat(inboundResponseFromIdp.getAuthnStatementAssertion().get()).isEqualTo(passthroughAuthnAssertion);
        assertThat(inboundResponseFromIdp.getMatchingDatasetAssertion().get()).isEqualTo(passthroughMdsAssertion);
    }
}
