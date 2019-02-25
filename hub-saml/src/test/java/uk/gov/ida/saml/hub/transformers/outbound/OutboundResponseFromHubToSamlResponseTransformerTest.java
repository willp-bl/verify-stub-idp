package uk.gov.ida.saml.hub.transformers.outbound;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;
import stubidp.saml.utils.core.domain.PassthroughAssertion;
import stubidp.saml.utils.core.test.OpenSAMLMockitoRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static stubidp.saml.utils.core.test.builders.AssertionBuilder.anAssertion;
import static uk.gov.ida.saml.core.test.builders.PassthroughAssertionBuilder.aPassthroughAssertion;
import static stubidp.saml.utils.core.test.builders.ResponseBuilder.aResponse;
import static stubidp.saml.utils.core.test.builders.ResponseForHubBuilder.anAuthnResponse;

@RunWith(OpenSAMLMockitoRunner.class)
public class OutboundResponseFromHubToSamlResponseTransformerTest {
    @Mock
    private TransactionIdaStatusMarshaller statusMarshaller = null;
    @Mock
    private EncryptedAssertionUnmarshaller encryptedAssertionUnmarshaller;
    private OutboundResponseFromHubToSamlResponseTransformer transformer;

    @Before
    public void setup() {
        OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
        transformer = new OutboundResponseFromHubToSamlResponseTransformer(statusMarshaller, openSamlXmlObjectFactory, encryptedAssertionUnmarshaller);
    }

    @Test
    public void transformAssertions_shouldTransformMatchingServiceAssertions() throws Exception {
        PassthroughAssertion matchingServiceAssertion = aPassthroughAssertion().buildMatchingServiceAssertion();
        Response transformedResponse = aResponse().withNoDefaultAssertion().build();
        EncryptedAssertion transformedMatchingDatasetAssertion = anAssertion().build();
        when(encryptedAssertionUnmarshaller.transform(matchingServiceAssertion.getUnderlyingAssertionBlob())).thenReturn(transformedMatchingDatasetAssertion);

        String encryptedMatchingServiceAssertion = matchingServiceAssertion.getUnderlyingAssertionBlob();
        transformer.transformAssertions(anAuthnResponse().withEncryptedAssertions(Arrays.asList(encryptedMatchingServiceAssertion)).buildOutboundResponseFromHub(), transformedResponse);

        assertThat(transformedResponse.getEncryptedAssertions().size()).isEqualTo(1);
        assertThat(transformedResponse.getEncryptedAssertions().get(0)).isEqualTo(transformedMatchingDatasetAssertion);
    }
}
