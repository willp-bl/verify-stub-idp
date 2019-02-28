package stubidp.eidas.metadata.support;

import java.security.PublicKey;
import java.util.List;

public interface SigningKeyStore {
    List<PublicKey> getVerifyingKeysForEntity(String entityId);
}
