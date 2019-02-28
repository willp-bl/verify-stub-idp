package stubidp.saml.hub.core.transformers.outbound;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.w3c.dom.Element;
import stubidp.saml.utils.core.test.OpenSAMLRunner;
import stubidp.saml.serializers.serializers.XmlObjectToElementTransformer;

import static org.assertj.core.api.Assertions.assertThat;
import static stubidp.saml.utils.core.test.builders.AuthnRequestBuilder.anAuthnRequest;
import static stubidp.saml.utils.core.test.builders.IssuerBuilder.anIssuer;

@RunWith(OpenSAMLRunner.class)
public class EntitiesDescriptorToElementTransformerTest {

    @Test
    public void transform_shouldTransformASamlObjectIntoAnElement() throws Exception {
        AuthnRequest authnRequest = anAuthnRequest().withIssuer(anIssuer().build()).build();
        XmlObjectToElementTransformer<AuthnRequest> transformer = new XmlObjectToElementTransformer<>();

        Element result = transformer.apply(authnRequest);

        assertThat(result).isNotNull();
    }
}
