package certificates.values;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CACertificates {
    private static String readCertificateFile(String name) {
        try {
            return IOUtils.toString(CACertificates.class.getResourceAsStream("/ca-certificates/" + name), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String TEST_ROOT_CA = readCertificateFile("ida-root-ca.pem.test");

    public static final String TEST_METADATA_CA = readCertificateFile("ida-metadata-ca.pem.test");

    public static final String TEST_IDP_CA = readCertificateFile("ida-intermediary-ca.pem.test");

    public static final String TEST_RP_CA = readCertificateFile("ida-intermediary-rp-ca.pem.test");

    public static final String TEST_CORE_CA = readCertificateFile("idap-core-ca.pem.test");
}
