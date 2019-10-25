package stubidp.eidas.metadata.support;

import java.security.PublicKey;

public interface EncryptionKeyStore {
    PublicKey getEncryptionKeyForEntity(String entityId);
}
