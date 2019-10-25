package stubidp.saml.utils.metadata.transformers;

import org.junit.jupiter.api.Test;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import stubidp.saml.utils.OpenSAMLRunner;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;
import stubidp.utils.security.security.Certificate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static stubidp.saml.utils.core.test.builders.CertificateBuilder.aCertificate;

public class KeyDescriptorsUnmarshallerTest extends OpenSAMLRunner {

    private final KeyDescriptorsUnmarshaller factory = new KeyDescriptorsUnmarshaller(new OpenSamlXmlObjectFactory());

    @Test
    public void transform_shouldTransformCertificate() throws Exception {
        Certificate certificate = aCertificate().build();
        List<KeyDescriptor> keyDescriptors = factory.fromCertificates(List.of(certificate));
        KeyDescriptor keyDescriptor = keyDescriptors.get(0);
        assertThat(keyDescriptor.getKeyInfo().getX509Datas().get(0).getX509Certificates().get(0).getValue()).isEqualTo(certificate.getCertificate());
    }

    @Test
    public void transform_shouldReturnExpectedNumberOfKeyDescriptors() {
        Certificate certificate = aCertificate().build();
        List<KeyDescriptor> keyDescriptors = factory.fromCertificates(List.of(certificate, certificate));
        assertThat(keyDescriptors.size()).isEqualTo(2);
    }
}
