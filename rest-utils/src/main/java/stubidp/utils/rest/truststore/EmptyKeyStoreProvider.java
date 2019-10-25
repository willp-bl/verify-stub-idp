package stubidp.utils.rest.truststore;


import com.google.common.base.Throwables;

import javax.inject.Inject;
import javax.inject.Provider;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class EmptyKeyStoreProvider implements Provider<KeyStore> {

    @Inject
    public EmptyKeyStoreProvider() { }

    @Override
    public KeyStore get() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            return keyStore;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
