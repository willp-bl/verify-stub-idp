package stubidp.utils.security.security;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class PublicKeyFileInputStreamFactory implements PublicKeyInputStreamFactory {

    @Inject
    public PublicKeyFileInputStreamFactory() { }

    public InputStream createInputStream(String publicKeyUri) {
        try {
            return new FileInputStream(new File(publicKeyUri));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
