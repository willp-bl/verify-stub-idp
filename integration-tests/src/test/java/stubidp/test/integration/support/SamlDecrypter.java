package stubidp.test.integration.support;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.slf4j.event.Level;
import stubidp.saml.extensions.validation.SamlTransformationErrorException;
import stubidp.saml.extensions.validation.SamlValidationSpecificationFailure;
import stubidp.saml.hub.core.errors.SamlTransformationErrorFactory;
import stubidp.saml.hub.core.validators.DestinationValidator;
import stubidp.saml.hub.core.validators.assertion.AssertionAttributeStatementValidator;
import stubidp.saml.hub.core.validators.assertion.AuthnStatementAssertionValidator;
import stubidp.saml.hub.core.validators.assertion.DuplicateAssertionValidatorImpl;
import stubidp.saml.hub.core.validators.assertion.IPAddressValidator;
import stubidp.saml.hub.core.validators.assertion.IdentityProviderAssertionValidator;
import stubidp.saml.hub.core.validators.assertion.MatchingDatasetAssertionValidator;
import stubidp.saml.hub.core.validators.subject.AssertionSubjectValidator;
import stubidp.saml.hub.core.validators.subjectconfirmation.AssertionSubjectConfirmationValidator;
import stubidp.saml.hub.hub.domain.InboundResponseFromIdp;
import stubidp.saml.hub.hub.transformers.inbound.IdaResponseFromIdpUnmarshaller;
import stubidp.saml.hub.hub.transformers.inbound.IdpIdaStatusUnmarshaller;
import stubidp.saml.hub.hub.transformers.inbound.PassthroughAssertionUnmarshaller;
import stubidp.saml.hub.hub.transformers.inbound.SamlStatusToCountryAuthenticationStatusCodeMapper;
import stubidp.saml.hub.hub.transformers.inbound.providers.DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer;
import stubidp.saml.hub.hub.validators.authnrequest.ConcurrentMapIdExpirationCache;
import stubidp.saml.hub.hub.validators.response.common.ResponseSizeValidator;
import stubidp.saml.hub.hub.validators.response.idp.components.EncryptedResponseFromIdpValidator;
import stubidp.saml.hub.hub.validators.response.idp.components.ResponseAssertionsFromIdpValidator;
import stubidp.saml.hub.metadata.IdpMetadataPublicKeyStore;
import stubidp.saml.metadata.JerseyClientMetadataResolver;
import stubidp.saml.metadata.factories.MetadataSignatureTrustEngineFactory;
import stubidp.saml.security.AssertionDecrypter;
import stubidp.saml.security.CredentialFactorySignatureValidator;
import stubidp.saml.security.DecrypterFactory;
import stubidp.saml.security.IdaKeyStore;
import stubidp.saml.security.IdaKeyStoreCredentialRetriever;
import stubidp.saml.security.MetadataBackedSignatureValidator;
import stubidp.saml.security.SamlAssertionsSignatureValidator;
import stubidp.saml.security.SamlMessageSignatureValidator;
import stubidp.saml.security.SigningCredentialFactory;
import stubidp.saml.security.validators.ValidatedResponse;
import stubidp.saml.security.validators.encryptedelementtype.EncryptionAlgorithmValidator;
import stubidp.saml.security.validators.issuer.IssuerValidator;
import stubidp.saml.security.validators.signature.SamlResponseSignatureValidator;
import stubidp.saml.serializers.deserializers.OpenSamlXMLObjectUnmarshaller;
import stubidp.saml.serializers.deserializers.StringToOpenSamlObjectTransformer;
import stubidp.saml.serializers.deserializers.parser.SamlObjectParser;
import stubidp.saml.serializers.deserializers.validators.Base64StringDecoder;
import stubidp.saml.serializers.deserializers.validators.NotNullSamlStringValidator;
import stubidp.saml.serializers.serializers.XmlObjectToBase64EncodedStringTransformer;
import stubidp.saml.utils.core.transformers.AuthnContextFactory;
import stubidp.saml.utils.hub.validators.StringSizeValidator;
import stubidp.stubidp.Urls;
import stubidp.stubidp.saml.EidasAuthnRequestValidator;
import stubidp.stubidp.saml.IdpAuthnRequestValidator;
import stubidp.test.integration.support.eidas.EidasAttributeStatementAssertionValidator;
import stubidp.test.integration.support.eidas.EidasAuthnResponseIssuerValidator;
import stubidp.test.integration.support.eidas.InboundResponseFromCountry;
import stubidp.utils.security.security.PrivateKeyFactory;
import stubidp.utils.security.security.PublicKeyFactory;
import stubidp.utils.security.security.X509CertificateFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.text.MessageFormat.format;
import static stubidp.test.devpki.TestCertificateStrings.HUB_CONNECTOR_TEST_PRIVATE_ENCRYPTION_KEY;
import static stubidp.test.devpki.TestCertificateStrings.HUB_CONNECTOR_TEST_PUBLIC_SIGNING_CERT;
import static stubidp.test.devpki.TestCertificateStrings.HUB_TEST_PRIVATE_ENCRYPTION_KEY;
import static stubidp.test.devpki.TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT;

/**
 * Be warned that this class does little to no validation and is just for testing the contents of a response
 */
public class SamlDecrypter {

    private final Client client;
    private final URI metadataUri;
    private final String hubEntityId;
    private final int localPort;

    // Manual Guice injection
    private final StringToOpenSamlObjectTransformer<Response> stringToOpenSamlObjectTransformer = new StringToOpenSamlObjectTransformer<>(new NotNullSamlStringValidator(),
            new Base64StringDecoder(),
            new ResponseSizeValidator(new StringSizeValidator()),
            new OpenSamlXMLObjectUnmarshaller<>(new SamlObjectParser()));
    private final Optional<String> eidasSchemeName;
    private final URI assertionConsumerServices;
    private final boolean checkKeyInfo;

    public SamlDecrypter(Client client, URI metadataUri, String hubEntityId, int localPort, Optional<String> eidasSchemeName, URI assertionConsumerServices) {
        this.client = client;
        this.metadataUri = metadataUri;
        this.hubEntityId = hubEntityId;
        this.localPort = localPort;
        this.eidasSchemeName = eidasSchemeName;
        this.assertionConsumerServices = assertionConsumerServices;
        this.checkKeyInfo = false;
    }

    public SamlDecrypter(Client client, URI metadataUri, String hubEntityId, int localPort, Optional<String> eidasSchemeName, URI assertionConsumerServices, boolean checkKeyInfo) {
        this.client = client;
        this.metadataUri = metadataUri;
        this.hubEntityId = hubEntityId;
        this.localPort = localPort;
        this.eidasSchemeName = eidasSchemeName;
        this.assertionConsumerServices = assertionConsumerServices;
        this.checkKeyInfo = checkKeyInfo;
    }

    /**
     * Be warned that this method does little to no validation and is just for testing the contents of a response
     */
    public InboundResponseFromIdp decryptSaml(String samlResponse) {
        final JerseyClientMetadataResolver jerseyClientMetadataResolver = getMetadataResolver(metadataUri);
        final SigningCredentialFactory credentialFactory = new SigningCredentialFactory(new AuthnResponseKeyStore(new IdpMetadataPublicKeyStore(jerseyClientMetadataResolver)));
        DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer decoratedSamlResponseToIdaResponseIssuedByIdpTransformer
                = buildDecoratedSamlResponseToIdaResponseIssuedByIdpTransformer(credentialFactory, createHubKeyStore());

        final org.opensaml.saml.saml2.core.Response response = stringToOpenSamlObjectTransformer.apply(samlResponse);
        if(checkKeyInfo) {
            EidasAuthnRequestValidator.validateKeyInfo(response);
        } else {
            IdpAuthnRequestValidator.validateKeyInfo(response);
        }
        return decoratedSamlResponseToIdaResponseIssuedByIdpTransformer.apply(response);
    }

    private JerseyClientMetadataResolver getMetadataResolver(URI metadataUri) {
        final JerseyClientMetadataResolver jerseyClientMetadataResolver = new JerseyClientMetadataResolver(null,  client, metadataUri);
        try {
            // a parser pool needs to be provided
            BasicParserPool pool = new BasicParserPool();
            pool.initialize();
            jerseyClientMetadataResolver.setParserPool(pool);
            jerseyClientMetadataResolver.setId("SamlDecrypter.MetadataResolver"+UUID.randomUUID());
            jerseyClientMetadataResolver.initialize();
            jerseyClientMetadataResolver.refresh();
        } catch (ComponentInitializationException | ResolverException e) {
            e.printStackTrace();
        }
        return jerseyClientMetadataResolver;
    }

    public InboundResponseFromCountry decryptEidasSaml(String samlResponse) {
        return decryptEidasSaml(samlResponse, true);
    }

    public InboundResponseFromCountry decryptEidasSamlUnsignedAssertions(String samlResponse) {
        return decryptEidasSaml(samlResponse, false);
    }

    /**
     * Be warned that this method does little to no validation and is just for testing the contents of a response
     */
    private InboundResponseFromCountry decryptEidasSaml(String samlResponse, boolean signedAssertions) {
        Response response = stringToOpenSamlObjectTransformer.apply(samlResponse);
        EidasAuthnRequestValidator.validateKeyInfo(response);
        ValidatedResponse validatedResponse = validateResponse(response);
        AssertionDecrypter assertionDecrypter = getAES256WithGCMAssertionDecrypter(createEidasKeyStore());
        List<Assertion> assertions = assertionDecrypter.decryptAssertions(validatedResponse);
        Optional<Assertion> validatedIdentityAssertion = validateAssertion(validatedResponse, assertions, signedAssertions);

        return new InboundResponseFromCountry(response.getIssuer().getValue(),
                validatedIdentityAssertion.get(),
                response.getStatus()
        );
    }

    private AssertionDecrypter getAES256WithGCMAssertionDecrypter(IdaKeyStore keyStore) {
        IdaKeyStoreCredentialRetriever idaKeyStoreCredentialRetriever = new IdaKeyStoreCredentialRetriever(keyStore);
        Decrypter decrypter = new DecrypterFactory().createDecrypter(idaKeyStoreCredentialRetriever.getDecryptingCredentials());
        return new AssertionDecrypter(
                new EncryptionAlgorithmValidator(
                    Set.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM),
                    Set.of(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP)),
                decrypter
        );
    }

    private ValidatedResponse validateResponse(Response response) {
        SamlResponseSignatureValidator samlResponseSignatureValidator = new SamlResponseSignatureValidator(getSamlMessageSignatureValidator(hubEntityId));
        final ValidatedResponse validatedResponse = samlResponseSignatureValidator.validate(response, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        new DestinationValidator(UriBuilder.fromUri(assertionConsumerServices).replacePath(null).build(), assertionConsumerServices.getPath()).validate(response.getDestination());
        return validatedResponse;
    }

    private void getValidatedAssertion(List<Assertion> decryptedAssertions, boolean signedAssertions) {
        SamlAssertionsSignatureValidator samlAssertionsSignatureValidator = new SamlAssertionsSignatureValidator(getSamlMessageSignatureValidator(hubEntityId));
        try {
            samlAssertionsSignatureValidator.validate(decryptedAssertions, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        } catch(SamlTransformationErrorException e) {
            if(signedAssertions) {
                throw e;
            }
            if(!e.getMessage().contains("Message has no signature")) {
                throw e;
            }
        }
    }

    private void responseAssertionFromCountryValidatorValidate(ValidatedResponse validatedResponse, Assertion validatedIdentityAssertion, boolean signedAssertions) {

        new IdentityProviderAssertionValidator(
                new IssuerValidator(),
                new AssertionSubjectValidator(),
                new AssertionAttributeStatementValidator(),
                new AssertionSubjectConfirmationValidator(),
                signedAssertions
        ).validate(validatedIdentityAssertion, validatedResponse.getInResponseTo(), assertionConsumerServices.toASCIIString());

        if (validatedResponse.isSuccess()) {

            if (validatedIdentityAssertion.getAuthnStatements().size() > 1) {
                SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.multipleAuthnStatements();
                throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
            }

            new EidasAttributeStatementAssertionValidator().validate(validatedIdentityAssertion);
            new AuthnStatementAssertionValidator(
                    new DuplicateAssertionValidatorImpl(new ConcurrentMapIdExpirationCache<>(new ConcurrentHashMap<>()))
            ).validate(validatedIdentityAssertion);
            new EidasAuthnResponseIssuerValidator().validate(validatedResponse, validatedIdentityAssertion);
        }
    }

    private SamlMessageSignatureValidator getSamlMessageSignatureValidator(String entityId) {
        return Optional.of(getMetadataResolver(UriBuilder.fromUri("http://localhost:"+localPort+ Urls.EIDAS_METADATA_RESOURCE).build(eidasSchemeName.get())))
                .map(m -> {
                    try {
                        return new MetadataSignatureTrustEngineFactory().createSignatureTrustEngine(m);
                    } catch (ComponentInitializationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(MetadataBackedSignatureValidator::withoutCertificateChainValidation)
                .map(SamlMessageSignatureValidator::new)
                .orElseThrow(() -> new SamlTransformationErrorException(format("Unable to find metadata resolver for entity Id {0}", entityId), Level.ERROR));
    }

    private Optional<Assertion> validateAssertion(ValidatedResponse validatedResponse, List<Assertion> decryptedAssertions, boolean signedAssertions) {
        getValidatedAssertion(decryptedAssertions, signedAssertions);
        Optional<Assertion> identityAssertion = decryptedAssertions.stream().findFirst();
        identityAssertion.ifPresent(assertion -> responseAssertionFromCountryValidatorValidate(validatedResponse, assertion, signedAssertions));
        return identityAssertion;
    }

    //FIXME: allow this to be set by the test class
    private IdaKeyStore createHubKeyStore() {
        PrivateKey privateKey = new PrivateKeyFactory().createPrivateKey(Base64.getDecoder().decode(HUB_TEST_PRIVATE_ENCRYPTION_KEY));

        PublicKey publicKey = new PublicKeyFactory(new X509CertificateFactory()).createPublicKey(HUB_TEST_PUBLIC_ENCRYPTION_CERT);

        List<KeyPair> encryptionKeys = List.of(new KeyPair(publicKey, privateKey));
        return new IdaKeyStore(null, encryptionKeys);
    }

    //FIXME: allow this to be set by the test class
    private IdaKeyStore createEidasKeyStore() {
        PrivateKey privateKey = new PrivateKeyFactory().createPrivateKey(Base64.getDecoder().decode(HUB_CONNECTOR_TEST_PRIVATE_ENCRYPTION_KEY));

        PublicKey publicKey = new PublicKeyFactory(new X509CertificateFactory()).createPublicKey(HUB_CONNECTOR_TEST_PUBLIC_SIGNING_CERT);

        List<KeyPair> encryptionKeys = List.of(new KeyPair(publicKey, privateKey));
        return new IdaKeyStore(null, encryptionKeys);
    }

    private DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer buildDecoratedSamlResponseToIdaResponseIssuedByIdpTransformer(SigningCredentialFactory credentialFactory, IdaKeyStore keyStore) {
        IdaKeyStoreCredentialRetriever storeCredentialRetriever = new IdaKeyStoreCredentialRetriever(keyStore);
        return new DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer(
                new IdaResponseFromIdpUnmarshaller(
                        new IdpIdaStatusUnmarshaller(),
                        new PassthroughAssertionUnmarshaller(new XmlObjectToBase64EncodedStringTransformer<>(), new AuthnContextFactory())),
                new SamlResponseSignatureValidator(new SamlMessageSignatureValidator(new CredentialFactorySignatureValidator(credentialFactory))),
                new AssertionDecrypter(new EncryptionAlgorithmValidator(), new DecrypterFactory().createDecrypter(storeCredentialRetriever.getDecryptingCredentials())),
                new SamlAssertionsSignatureValidator(new SamlMessageSignatureValidator(new CredentialFactorySignatureValidator(credentialFactory))),
                new EncryptedResponseFromIdpValidator<>(new SamlStatusToCountryAuthenticationStatusCodeMapper()),
                new DestinationValidator(UriBuilder.fromUri(assertionConsumerServices).replacePath(null).build(), assertionConsumerServices.getPath()),
                new ResponseAssertionsFromIdpValidator(
                        new IdentityProviderAssertionValidator(
                                new IssuerValidator(),
                                new AssertionSubjectValidator(),
                                new AssertionAttributeStatementValidator(),
                                new AssertionSubjectConfirmationValidator()),
                        new MatchingDatasetAssertionValidator(new DuplicateAssertionValidatorImpl(new ConcurrentMapIdExpirationCache<>(new ConcurrentHashMap<>()))),
                        new AuthnStatementAssertionValidator(new DuplicateAssertionValidatorImpl(new ConcurrentMapIdExpirationCache<>(new ConcurrentHashMap<>()))),
                        new IPAddressValidator(),
                        hubEntityId));
    }
}
