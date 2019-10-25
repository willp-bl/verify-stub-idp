package stubidp.saml.security.saml.builders;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import stubidp.saml.security.EncrypterFactory;
import stubidp.saml.security.saml.TestCredentialFactory;
import stubidp.test.devpki.TestCertificateStrings;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class EncryptedAssertionBuilder {

    private String id = "some-assertion-id";
    private TestCredentialFactory credentialFactory = new TestCredentialFactory(TestCertificateStrings.TEST_PUBLIC_CERT, null);
    private Optional<Credential> credential = ofNullable(credentialFactory.getEncryptingCredential());

    public static EncryptedAssertionBuilder anEncryptedAssertionBuilder() {
        return new EncryptedAssertionBuilder();
    }

    public EncryptedAssertion build() {
        Assertion assertion = AssertionBuilder.anAssertion().withId(id).build();
        Encrypter encrypter = new EncrypterFactory().createEncrypter(credential.get());
        try {
            return encrypter.encrypt(assertion);
        } catch (EncryptionException e) {
            throw new RuntimeException(e);
        }
    }

    public EncryptedAssertionBuilder withEncrypterCredential(Credential credential) {
        this.credential = ofNullable(credential);
        return this;
    }

    public EncryptedAssertionBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public EncryptedAssertionBuilder withPublicEncryptionCert(String cert) {
        this.credential = ofNullable(new TestCredentialFactory(cert, null).getEncryptingCredential());
        return this;
    }
}
