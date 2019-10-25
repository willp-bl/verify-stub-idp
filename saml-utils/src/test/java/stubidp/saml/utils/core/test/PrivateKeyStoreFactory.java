package stubidp.saml.utils.core.test;

import org.apache.commons.codec.binary.Base64;
import stubidp.test.devpki.TestCertificateStrings;
import stubidp.utils.security.security.PrivateKeyFactory;
import stubidp.utils.security.security.PrivateKeyStore;

import java.security.PrivateKey;
import java.util.List;
import java.util.stream.Collectors;

public class PrivateKeyStoreFactory {
    public static final String TEST_ENTITY_ID = "https://signin.service.gov.uk";

    public PrivateKeyStore create(String entityId) {
        PrivateKey privateSigningKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.PRIVATE_SIGNING_KEYS.get(entityId)));
        List<String> encryptionKeyStrings = TestCertificateStrings.PRIVATE_ENCRYPTION_KEYS.get(entityId);
        List<PrivateKey> privateEncryptionKeys = encryptionKeyStrings.stream()
            .map(input -> new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(input)))
            .collect(Collectors.toList());
        return new PrivateKeyStore(privateSigningKey, privateEncryptionKeys);
    }
}
