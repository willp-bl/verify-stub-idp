package stubidp.saml.hub.hub.api;

import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import stubidp.saml.extensions.IdaSamlBootstrap;
import stubidp.saml.hub.hub.test.builders.EidasAuthnRequestBuilder;
import stubidp.saml.hub.hub.test.builders.IdaAuthnRequestBuilder;
import stubidp.saml.security.IdaKeyStore;
import stubidp.saml.serializers.deserializers.StringToOpenSamlObjectTransformer;
import stubidp.saml.utils.core.api.CoreTransformersFactory;
import stubidp.saml.utils.core.domain.AuthnContext;
import stubidp.saml.utils.hub.domain.EidasAuthnRequestFromHub;
import stubidp.saml.utils.hub.domain.IdaAuthnRequestFromHub;
import stubidp.test.devpki.TestCertificateStrings;
import stubidp.utils.security.security.PrivateKeyFactory;
import stubidp.utils.security.security.PublicKeyFactory;
import stubidp.utils.security.security.X509CertificateFactory;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class HubTransformersFactoryTest {

    private StringToOpenSamlObjectTransformer<AuthnRequest> stringtoOpenSamlObjectTransformer;

    private final SignatureAlgorithm signatureAlgorithm = new SignatureRSASHA256();
    private final DigestAlgorithm digestAlgorithm = new DigestSHA256();
    private final X509Certificate hubSigningCert = new X509CertificateFactory().createCertificate(TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT);

    @BeforeEach
    public void setUp() {
        IdaSamlBootstrap.bootstrap();
        CoreTransformersFactory coreTransformersFactory = new CoreTransformersFactory();
        stringtoOpenSamlObjectTransformer = coreTransformersFactory.
            getStringtoOpenSamlObjectTransformer(input -> {});
    }

    @Test
    public void shouldNotContainKeyInfoInIdaAuthnRequest() throws Exception {
        Function<IdaAuthnRequestFromHub, String> eidasTransformer = new HubTransformersFactory().getIdaAuthnRequestFromHubToStringTransformer(
            getKeyStore(hubSigningCert),
            signatureAlgorithm,
            digestAlgorithm
        );
        IdaAuthnRequestFromHub idaAuthnRequestFromHub = IdaAuthnRequestBuilder.anIdaAuthnRequest()
            .withLevelsOfAssurance(Collections.singletonList(AuthnContext.LEVEL_3))
            .buildFromHub();

        String apply = eidasTransformer.apply(idaAuthnRequestFromHub);

        assertThat(apply).isNotNull();

        AuthnRequest authnReq = stringtoOpenSamlObjectTransformer.apply(apply);
        assertThat(authnReq).isNotNull();
        assertThat(authnReq.getSignature().getKeyInfo()).withFailMessage("The Authn Request does not contain a KeyInfo section for Verify UK").isNull();
    }

    @Test
    public void shouldContainKeyInfoInEidasAuthnRequestWhenHubSignCertIsPresent() throws Exception {
        Function<EidasAuthnRequestFromHub, String> eidasTransformer = new HubTransformersFactory().getEidasAuthnRequestFromHubToStringTransformer(
            getKeyStore(hubSigningCert),
            signatureAlgorithm,
            digestAlgorithm
        );
        EidasAuthnRequestFromHub eidasAuthnRequestFromHub = EidasAuthnRequestBuilder.anEidasAuthnRequest()
            .withLevelsOfAssurance(Collections.singletonList(AuthnContext.LEVEL_2))
            .buildFromHub();

        String apply = eidasTransformer.apply(eidasAuthnRequestFromHub);

        assertThat(apply).isNotNull();

        AuthnRequest authnReq = stringtoOpenSamlObjectTransformer.apply(apply);
        assertThat(authnReq).isNotNull();
        assertThat(authnReq.getSignature().getKeyInfo()).withFailMessage("The Authn Request contains a KeyInfo section for eIDAS").isNotNull();
    }

    @Test
    public void shouldThrowExceptionWhenKeyInfoIsRequiredButSigningCertIsNotPresent() throws Base64DecodingException {
        Function<EidasAuthnRequestFromHub, String> eidasTransformer = new HubTransformersFactory().getEidasAuthnRequestFromHubToStringTransformer(
                getKeyStore(null),
                signatureAlgorithm,
                digestAlgorithm
        );
        EidasAuthnRequestFromHub eidasAuthnRequestFromHub = EidasAuthnRequestBuilder.anEidasAuthnRequest()
                .withLevelsOfAssurance(Collections.singletonList(AuthnContext.LEVEL_2))
                .buildFromHub();

        final Exception e = Assertions.assertThrows(Exception.class, () -> eidasTransformer.apply(eidasAuthnRequestFromHub));
        assertThat(e.getMessage()).isEqualTo("Unable to generate key info without a signing certificate");
    }

    private static IdaKeyStore getKeyStore(X509Certificate hubSigningCert) throws Base64DecodingException {
        List<KeyPair> encryptionKeyPairs = new ArrayList<>();
        PublicKeyFactory publicKeyFactory = new PublicKeyFactory(new X509CertificateFactory());
        PrivateKeyFactory privateKeyFactory = new PrivateKeyFactory();
        PublicKey encryptionPublicKey = publicKeyFactory.createPublicKey(TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT);
        PrivateKey encryptionPrivateKey = privateKeyFactory.createPrivateKey(Base64.decode(TestCertificateStrings.HUB_TEST_PRIVATE_ENCRYPTION_KEY.getBytes()));
        encryptionKeyPairs.add(new KeyPair(encryptionPublicKey, encryptionPrivateKey));
        PublicKey publicSigningKey = publicKeyFactory.createPublicKey(TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT);
        PrivateKey privateSigningKey = privateKeyFactory.createPrivateKey(Base64.decode(TestCertificateStrings.HUB_TEST_PRIVATE_SIGNING_KEY.getBytes()));
        KeyPair signingKeyPair = new KeyPair(publicSigningKey, privateSigningKey);

        return new IdaKeyStore(hubSigningCert, signingKeyPair, encryptionKeyPairs);
    }
}
