package stubidp.stubidp.security;

import stubidp.stubidp.repositories.MetadataRepository;
import stubidp.utils.security.security.PublicKeyFactory;
import stubidp.saml.security.SigningKeyStore;
import stubidp.stubidp.StubIdpBinder;

import javax.inject.Inject;
import javax.inject.Named;
import java.security.PublicKey;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class IdaAuthnRequestKeyStore implements SigningKeyStore {
    private final MetadataRepository metadataRepository;
    private final PublicKeyFactory publicKeyFactory;

    @Inject
    public IdaAuthnRequestKeyStore(@Named(StubIdpBinder.HUB_METADATA_REPOSITORY) MetadataRepository metadataRepository, PublicKeyFactory publicKeyFactory) {
        this.metadataRepository = metadataRepository;
        this.publicKeyFactory = publicKeyFactory;
    }

    @Override
    public List<PublicKey> getVerifyingKeysForEntity(String entityId) {
        List<PublicKey> keys = newArrayList();
        for (String encodedCertificate : metadataRepository.getSigningCertificates()) {
            keys.add(publicKeyFactory.createPublicKey(encodedCertificate));
        }
        return keys;
    }
}
