package stubidp.saml.security;

import com.google.common.collect.ImmutableList;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import stubidp.saml.security.errors.SamlTransformationErrorFactory;
import stubidp.saml.security.exception.SamlFailedToDecryptException;
import stubidp.saml.security.validators.ValidatedEncryptedAssertionContainer;
import stubidp.saml.security.validators.encryptedelementtype.EncryptionAlgorithmValidator;

import java.util.List;

public class AssertionDecrypter {

    protected final EncryptionAlgorithmValidator encryptionAlgorithmValidator;
    private Decrypter decrypter;

    public AssertionDecrypter(EncryptionAlgorithmValidator encryptionAlgorithmValidator, Decrypter decrypter) {
        this.encryptionAlgorithmValidator = encryptionAlgorithmValidator;
        this.decrypter = decrypter;
    }

    public List<Assertion> decryptAssertions(ValidatedEncryptedAssertionContainer container) {
        final List<EncryptedAssertion> encryptedAssertions = container.getEncryptedAssertions();
        final ImmutableList.Builder<Assertion> assertions = ImmutableList.builder();

        if (encryptedAssertions.isEmpty()) return assertions.build();

        decrypter.setRootInNewDocument(true);

        for (EncryptedAssertion encryptedAssertion : encryptedAssertions) {
            try {
                encryptionAlgorithmValidator.validate(encryptedAssertion);
                assertions.add(decrypter.decrypt(encryptedAssertion));
            } catch (DecryptionException e) {
                String message = "Problem decrypting assertion " + encryptedAssertion + ".";
                throw new SamlFailedToDecryptException(SamlTransformationErrorFactory.unableToDecrypt(message), e);
            }
        }

        return assertions.build();
    }
}
