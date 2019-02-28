package stubidp.stubidp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.hubspot.dropwizard.guicier.DropwizardAwareModule;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.AbstractReloadingMetadataResolver;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.w3c.dom.Document;
import stubidp.utils.security.configuration.KeyConfiguration;
import stubidp.utils.security.configuration.SecureCookieConfiguration;
import stubidp.utils.security.configuration.SecureCookieKeyConfiguration;
import stubidp.utils.security.configuration.SecureCookieKeyStore;
import stubidp.utils.security.security.HmacDigest;
import stubidp.utils.security.security.IdGenerator;
import stubidp.utils.security.security.PublicKeyFactory;
import stubidp.utils.security.security.SecureCookieKeyConfigurationKeyStore;
import stubidp.utils.security.security.X509CertificateFactory;
import stubidp.utils.rest.jerseyclient.ErrorHandlingClient;
import stubidp.utils.rest.jerseyclient.JsonClient;
import stubidp.utils.rest.jerseyclient.JsonResponseProcessor;
import stubidp.utils.rest.restclient.ClientProvider;
import stubidp.saml.utils.core.api.CoreTransformersFactory;
import stubidp.saml.utils.hub.domain.IdaAuthnRequestFromHub;
import stubidp.saml.stubidp.configuration.SamlConfiguration;
import stubidp.saml.metadata.MetadataHealthCheck;
import stubidp.saml.metadata.MetadataResolverConfiguration;
import stubidp.saml.metadata.factories.DropwizardMetadataResolverFactory;
import stubidp.saml.security.EncryptionKeyStore;
import stubidp.saml.security.EntityToEncryptForLocator;
import stubidp.saml.security.IdaKeyStore;
import stubidp.saml.security.IdaKeyStoreCredentialRetriever;
import stubidp.saml.security.SignatureFactory;
import stubidp.saml.security.SigningKeyStore;
import stubidp.saml.security.signature.SignatureRSASSAPSS;
import stubidp.stubidp.auth.ManagedAuthFilterInstaller;
import stubidp.stubidp.builders.CountryMetadataBuilder;
import stubidp.stubidp.builders.CountryMetadataSigningHelper;
import stubidp.stubidp.configuration.AssertionLifetimeConfiguration;
import stubidp.stubidp.configuration.IdpStubsConfiguration;
import stubidp.stubidp.configuration.SigningKeyPairConfiguration;
import stubidp.stubidp.configuration.SingleIdpConfiguration;
import stubidp.stubidp.configuration.StubIdpConfiguration;
import stubidp.stubidp.cookies.CookieFactory;
import stubidp.stubidp.cookies.HmacValidator;
import stubidp.stubidp.domain.factories.AssertionFactory;
import stubidp.stubidp.domain.factories.AssertionRestrictionsFactory;
import stubidp.stubidp.domain.factories.IdentityProviderAssertionFactory;
import stubidp.stubidp.domain.factories.StubTransformersFactory;
import stubidp.stubidp.listeners.StubIdpsFileListener;
import stubidp.stubidp.repositories.AllIdpsUserRepository;
import stubidp.stubidp.repositories.EidasSessionRepository;
import stubidp.stubidp.repositories.IdpSessionRepository;
import stubidp.stubidp.repositories.IdpStubsRepository;
import stubidp.stubidp.repositories.MetadataRepository;
import stubidp.stubidp.repositories.StubCountryRepository;
import stubidp.stubidp.repositories.UserRepository;
import stubidp.stubidp.repositories.jdbc.JDBIEidasSessionRepository;
import stubidp.stubidp.repositories.jdbc.JDBIIdpSessionRepository;
import stubidp.stubidp.repositories.jdbc.JDBIUserRepository;
import stubidp.stubidp.repositories.jdbc.UserMapper;
import stubidp.stubidp.repositories.reaper.ManagedStaleSessionReaper;
import stubidp.stubidp.saml.locators.IdpHardCodedEntityToEncryptForLocator;
import stubidp.stubidp.saml.transformers.EidasResponseTransformerProvider;
import stubidp.stubidp.saml.transformers.OutboundResponseFromIdpTransformerProvider;
import stubidp.stubidp.security.HubEncryptionKeyStore;
import stubidp.stubidp.security.IdaAuthnRequestKeyStore;
import stubidp.stubidp.services.AuthnRequestReceiverService;
import stubidp.stubidp.services.EidasAuthnResponseService;
import stubidp.stubidp.services.GeneratePasswordService;
import stubidp.stubidp.services.IdpUserService;
import stubidp.stubidp.services.NonSuccessAuthnResponseService;
import stubidp.stubidp.services.ServiceListService;
import stubidp.stubidp.services.StubCountryService;
import stubidp.stubidp.services.SuccessAuthnResponseService;
import stubidp.stubidp.services.UserService;
import stubidp.stubidp.views.SamlResponseRedirectViewFactory;
import stubidp.utils.rest.truststore.EmptyKeyStoreProvider;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.Validator;
import javax.ws.rs.client.Client;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class StubIdpModule extends DropwizardAwareModule<StubIdpConfiguration> {

    public static final String HUB_CONNECTOR_METADATA_REPOSITORY = "HubConnectorMetadataRepository";
    public static final String HUB_METADATA_REPOSITORY = "HubMetadataRepository";
    public static final String HUB_CONNECTOR_METADATA_RESOLVER = "HubConnectorMetadataResolver";
    public static final String HUB_METADATA_RESOLVER = "HubMetadataResolver";
    public static final String HUB_CONNECTOR_ENCRYPTION_KEY_STORE = "HubConnectorEncryptionKeyStore";
    public static final String HUB_ENCRYPTION_KEY_STORE = "HubEncryptionKeyStore";
    public static final String COUNTRY_SIGNING_KEY_STORE = "CountrySigningKeyStore";
    public static final String IDP_SIGNING_KEY_STORE = "IdpSigningKeyStore";

    @Override
    public void configure(Binder binder) {
        binder.bind(AssertionLifetimeConfiguration.class).to(StubIdpConfiguration.class).asEagerSingleton();

        binder.bind(SigningKeyStore.class).to(IdaAuthnRequestKeyStore.class).asEagerSingleton();

        binder.bind(EntityToEncryptForLocator.class).to(IdpHardCodedEntityToEncryptForLocator.class).asEagerSingleton();
        binder.bind(CountryMetadataSigningHelper.class).asEagerSingleton();
        binder.bind(new TypeLiteral<ConcurrentMap<String, Document>>() {
        }).toInstance(new ConcurrentHashMap<>());

        binder.bind(AllIdpsUserRepository.class).asEagerSingleton();

        binder.bind(IdpStubsRepository.class).asEagerSingleton();
        binder.bind(KeyStore.class).toProvider(EmptyKeyStoreProvider.class).asEagerSingleton();

        binder.bind(PublicKeyFactory.class);
        binder.bind(SamlResponseRedirectViewFactory.class);
        binder.bind(AssertionFactory.class);
        binder.bind(AssertionRestrictionsFactory.class);
        binder.bind(IdentityProviderAssertionFactory.class);
        binder.bind(CountryMetadataBuilder.class);

        binder.bind(StubIdpsFileListener.class).asEagerSingleton();

        //must be eager singletons to be auto injected
        // Elegant-hack: this is how we install the basic auth filter, so we can use a guice injected user repository
        binder.bind(ManagedAuthFilterInstaller.class).asEagerSingleton();

        binder.bind(IdGenerator.class);
        binder.bind(X509CertificateFactory.class);

        binder.bind(AuthnRequestReceiverService.class);
        binder.bind(SuccessAuthnResponseService.class);
        binder.bind(GeneratePasswordService.class);
        binder.bind(NonSuccessAuthnResponseService.class);
        binder.bind(IdpUserService.class);
        binder.bind(StubCountryService.class);
        binder.bind(UserService.class);
        binder.bind(SamlResponseRedirectViewFactory.class);

        binder.bind(ManagedStaleSessionReaper.class).asEagerSingleton();

        binder.bind(HmacValidator.class);
        binder.bind(HmacDigest.class);
        binder.bind(SecureCookieKeyStore.class).to(SecureCookieKeyConfigurationKeyStore.class);
        binder.bind(CookieFactory.class);
        binder.bind(JsonResponseProcessor.class);
    }

    @Provides
    public ObjectMapper getObjectMapper() {
        return getBootstrap().getObjectMapper();
    }

    @Provides
    public UserMapper getUserMapper(ObjectMapper objectMapper) {
        return new UserMapper(objectMapper);
    }

    @Provides
    @Singleton
    public UserRepository getUserRepository(UserMapper userMapper) {
        Jdbi jdbi = Jdbi.create(getConfiguration().getDatabaseConfiguration().getUrl());
        return new JDBIUserRepository(jdbi, userMapper);
    }
    
    @Provides
    @Singleton
    public IdpSessionRepository getIdpSessionRepository() {
        Jdbi jdbi = Jdbi.create(getConfiguration().getDatabaseConfiguration().getUrl());
        return new JDBIIdpSessionRepository(jdbi);
    }

    @Provides
    @Singleton
    public EidasSessionRepository getEidasSessionRepository() {
        Jdbi jdbi = Jdbi.create(getConfiguration().getDatabaseConfiguration().getUrl());
        return new JDBIEidasSessionRepository(jdbi);
    }

    @Provides
    @Singleton
    @Named("HubEntityId")
    public String getHubEntityId() {
        return getConfiguration().getHubEntityId();
    }

    @Provides
    @Singleton
    @Named("HubConnectorEntityId")
    public String getHubConnectorEntityId() {
        return getConfiguration().getEuropeanIdentityConfiguration().getHubConnectorEntityId();
    }

    @Provides
    @Singleton
    @Named("StubCountryMetadataUrl")
    public String getStubCountryMetadataUrl() {
        return getConfiguration().getEuropeanIdentityConfiguration().getStubCountryBaseUrl() + Urls.METADATA_RESOURCE;
    }

    @Provides
    @Singleton
    @Named("StubCountrySsoUrl")
    public String getStubCountrySsoUrl() {
        return getConfiguration().getEuropeanIdentityConfiguration().getStubCountryBaseUrl() + Urls.EIDAS_SAML2_SSO_RESOURCE;
    }

    @Provides
    private ConfigurationFactory<IdpStubsConfiguration> getConfigurationFactory() {
        Validator validator = getBootstrap().getValidatorFactory().getValidator();
        return new DefaultConfigurationFactoryFactory<IdpStubsConfiguration>()
            .create(IdpStubsConfiguration.class, validator, getBootstrap().getObjectMapper(), "");
    }

    @Provides
    @Singleton
    @Named("sessionCacheTimeoutInMinutes")
    public Integer getSessionCacheTimeoutInMinutes() {
        return 180;
    }

    @Provides
    private ConfigurationSourceProvider getConfigurationSourceProvider() {
        return getBootstrap().getConfigurationSourceProvider();
    }

    @Provides
    SignatureAlgorithm provideSignatureAlgorithm() {
        return new SignatureRSASHA256();
    }

    @Provides
    DigestAlgorithm provideDigestAlgorithm() {
        return new DigestSHA256();
    }

    @Provides
    @Named("countryMetadataSignatureFactory")
    private SignatureFactory getSignatureFactoryWithKeyInfo(@Named(COUNTRY_SIGNING_KEY_STORE) IdaKeyStore keyStore, DigestAlgorithm digestAlgorithm, SignatureAlgorithm signatureAlgorithm) {
        return new SignatureFactory(true, new IdaKeyStoreCredentialRetriever(keyStore), signatureAlgorithm, digestAlgorithm);
    }

    @Provides
    @Singleton
    public StubCountryRepository getStubCountryRepository(AllIdpsUserRepository allIdpsUserRepository, @Named("StubCountryMetadataUrl")String stubCountryMetadataUrl){
        return new StubCountryRepository(allIdpsUserRepository, stubCountryMetadataUrl);
    }

    @Provides
    @Named("metadataValidityPeriod")
    private ReadablePeriod getMetadataValidity() {
        return new Period().withYears(100);
    }

    @Provides
    public Function<String, IdaAuthnRequestFromHub> getStringToIdaAuthnRequestFromHubTransformer(SigningKeyStore signingKeyStore) {
        return new StubTransformersFactory().getStringToIdaAuthnRequestFromHub(
            signingKeyStore
        );
    }

    @Provides
    public Function<String, AuthnRequest> getStringToAuthnRequestTransformer() {
        return new StubTransformersFactory().getStringToAuthnRequest();
    }

    @Provides
    public OutboundResponseFromIdpTransformerProvider getOutboundResponseFromIdpTransformerProvider(
        @Named(StubIdpModule.HUB_ENCRYPTION_KEY_STORE) EncryptionKeyStore encryptionKeyStore,
        @Named(IDP_SIGNING_KEY_STORE) IdaKeyStore keyStore,
        EntityToEncryptForLocator entityToEncryptForLocator) {
        return new OutboundResponseFromIdpTransformerProvider(
            encryptionKeyStore,
            keyStore,
            entityToEncryptForLocator,
            Optional.ofNullable(getConfiguration().getSigningKeyPairConfiguration().getCert()),
            new StubTransformersFactory(),
            new SignatureRSASHA256(),
            new DigestSHA256()
        );
    }

    @Provides
    @Named("RSASHA256EidasAuthnResponseService")
    public EidasAuthnResponseService getECDSAEidasAuthnResponseService(
       @Named("HubConnectorEntityId") String hubConnectorEntityId,
       @Named("RSASHA256EidasResponseTransfomerProvider") EidasResponseTransformerProvider eidasResponseTransformerProvider,
       @Named(StubIdpModule.HUB_CONNECTOR_METADATA_REPOSITORY) Optional<MetadataRepository> metadataProvider,
       @Named("StubCountryMetadataUrl") String stubCountryMetadataUrl) {
        return new EidasAuthnResponseService(
                hubConnectorEntityId,
                eidasResponseTransformerProvider,
                metadataProvider,
                stubCountryMetadataUrl);
    }

    @Provides
    @Named("RSASHA256EidasResponseTransfomerProvider")
    public EidasResponseTransformerProvider getECDSAEidasResponseTransfomerProvider(
            @Named(StubIdpModule.HUB_CONNECTOR_ENCRYPTION_KEY_STORE) Optional<EncryptionKeyStore> encryptionKeyStore,
            @Named(COUNTRY_SIGNING_KEY_STORE) IdaKeyStore keyStore,
            EntityToEncryptForLocator entityToEncryptForLocator) {
        return new EidasResponseTransformerProvider(
                new CoreTransformersFactory(),
                encryptionKeyStore.orElse(null),
                keyStore,
                entityToEncryptForLocator,
                new SignatureRSASHA256(),
                new DigestSHA256()
        );
    }

    @Provides
    @Named("RSASSAPSSEidasAuthnResponseService")
    public EidasAuthnResponseService getRSASSAPSSEidasAuthnResponseService(
            @Named("HubConnectorEntityId") String hubConnectorEntityId,
            @Named("RSASSAPSSEidasResponseTransformerProvider") EidasResponseTransformerProvider eidasResponseTransformerProvider,
            @Named(StubIdpModule.HUB_CONNECTOR_METADATA_REPOSITORY) Optional<MetadataRepository> metadataProvider,
            @Named("StubCountryMetadataUrl") String stubCountryMetadataUrl) {
        return new EidasAuthnResponseService(
                hubConnectorEntityId,
                eidasResponseTransformerProvider,
                metadataProvider,
                stubCountryMetadataUrl);
    }

    @Named("RSASSAPSSEidasResponseTransformerProvider")
    @Provides
    public EidasResponseTransformerProvider getEidasResponseTransformerProvider(
        @Named(StubIdpModule.HUB_CONNECTOR_ENCRYPTION_KEY_STORE) Optional<EncryptionKeyStore> encryptionKeyStore,
        @Named(COUNTRY_SIGNING_KEY_STORE) IdaKeyStore keyStore,
        EntityToEncryptForLocator entityToEncryptForLocator) {
        return new EidasResponseTransformerProvider(
            new CoreTransformersFactory(),
            encryptionKeyStore.orElse(null),
            keyStore,
            entityToEncryptForLocator,
            new SignatureRSASSAPSS(),
            new DigestSHA256()
        );
    }

    @Provides
    @Singleton
    public SamlConfiguration samlConfiguration() {
        return getConfiguration().getSamlConfiguration();
    }

    @Provides
    @Singleton
    @Named(IDP_SIGNING_KEY_STORE)
    public IdaKeyStore getKeyStore() {
        return getKeystoreFromConfig(getConfiguration().getSigningKeyPairConfiguration());
    }

    @Provides
    @Singleton
    @Named(COUNTRY_SIGNING_KEY_STORE)
    public IdaKeyStore getCountryKeyStore() {
        return getKeystoreFromConfig(getConfiguration().getEuropeanIdentityConfiguration().getSigningKeyPairConfiguration());
    }

    @Provides
    @Singleton
    @Named("isSecureCookieEnabled")
    public Boolean isSecureCookieEnabled() {
        return Objects.nonNull(getConfiguration().getSecureCookieConfiguration());
    }

    @Provides
    @Singleton
    public HmacDigest.HmacSha256MacFactory getHmacSha256MacFactory() {
        return new HmacDigest.HmacSha256MacFactory();
    }

    @Provides
    @Singleton
    @SecureCookieKeyConfiguration
    public KeyConfiguration getSecureCookieKeyConfiguration() {
        return isSecureCookieEnabled() ? getConfiguration().getSecureCookieConfiguration().getKeyConfiguration() : new KeyConfiguration() {
        };
    }

    @Provides
    @Singleton
    public SecureCookieConfiguration getSecureCookieConfiguration() {
        return isSecureCookieEnabled() ? getConfiguration().getSecureCookieConfiguration() : new SecureCookieConfiguration() {
            {
                this.secure = false;
            }
        };
    }

    @Provides
    @Named(HUB_ENCRYPTION_KEY_STORE)
    @Singleton
    public EncryptionKeyStore getHubEncryptionKeyStore(@Named(HUB_METADATA_REPOSITORY) MetadataRepository metadataRepository, PublicKeyFactory publicKeyFactory) {
        return new HubEncryptionKeyStore(metadataRepository, publicKeyFactory);
    }

    @Provides
    @Named(HUB_CONNECTOR_ENCRYPTION_KEY_STORE)
    @Singleton
    public Optional<EncryptionKeyStore> getHubConnectorEncryptionKeyStore(@Named(HUB_CONNECTOR_METADATA_REPOSITORY) Optional<MetadataRepository> metadataRepository, PublicKeyFactory publicKeyFactory) {
        if (metadataRepository.isPresent()) {
            return Optional.of(new HubEncryptionKeyStore(metadataRepository.get(), publicKeyFactory));
        }
        return Optional.empty();
    }

    @Provides
    @Named(HUB_METADATA_REPOSITORY)
    @Singleton
    public MetadataRepository getHubMetadataRepository(@Named(HUB_METADATA_RESOLVER) MetadataResolver metadataResolver, @Named("HubEntityId") String hubEntityId) {
        return new MetadataRepository(metadataResolver, hubEntityId);
    }

    @Provides
    @Named(HUB_CONNECTOR_METADATA_REPOSITORY)
    @Singleton
    public Optional<MetadataRepository> getHubConnectorMetadataRepository(@Named(HUB_CONNECTOR_METADATA_RESOLVER) Optional<MetadataResolver> metadataResolver, @Named("HubConnectorEntityId") String hubEntityId) {
        if (metadataResolver.isPresent()) {
            return Optional.of(new MetadataRepository(metadataResolver.get(), hubEntityId));
        }
        return Optional.empty();
    }

    @Provides
    @Named(HUB_METADATA_RESOLVER)
    @Singleton
    public MetadataResolver getHubMetadataResolver() {
        MetadataResolver metadataResolver = new DropwizardMetadataResolverFactory().createMetadataResolver(getEnvironment(), getConfiguration().getMetadataConfiguration());
        registerMetadataHealthcheckAndRefresh(getEnvironment(), metadataResolver, getConfiguration().getMetadataConfiguration(), "metadata");
        return metadataResolver;
    }

    @Provides
    @Named(HUB_CONNECTOR_METADATA_RESOLVER)
    @Singleton
    public Optional<MetadataResolver> getHubConnectorMetadataResolver() {
        if (getConfiguration().getEuropeanIdentityConfiguration().isEnabled()) {
            MetadataResolver metadataResolver = new DropwizardMetadataResolverFactory().createMetadataResolver(getEnvironment(), getConfiguration().getEuropeanIdentityConfiguration().getMetadata());
            registerMetadataHealthcheckAndRefresh(getEnvironment(), metadataResolver, getConfiguration().getEuropeanIdentityConfiguration().getMetadata(), "connector-metadata");
            return Optional.of(metadataResolver);
        }
        return Optional.empty();
    }

    @Provides
    @Singleton
    public SingleIdpConfiguration getSingleIdpJourneyConfiguration() {
        return getConfiguration().getSingleIdpJourneyConfiguration();
    }

    @Provides
    @Singleton
    public JsonClient getJsonClient(JsonResponseProcessor jsonResponseProcessor) {
        Client client = new ClientProvider(
                getEnvironment(),
                getConfiguration().getSingleIdpJourneyConfiguration().getServiceListClient(),
                true,
                "StubIdpJsonClient").get();
        ErrorHandlingClient errorHandlingClient = new ErrorHandlingClient(client);
        return new JsonClient(errorHandlingClient, jsonResponseProcessor);
    }

    @Provides
    @Singleton
    public ServiceListService getServiceListService(JsonClient jsonClient) {
        return new ServiceListService(getConfiguration().getSingleIdpJourneyConfiguration(), jsonClient);
    }

    private void registerMetadataHealthcheckAndRefresh(Environment environment, MetadataResolver metadataResolver, MetadataResolverConfiguration metadataResolverConfiguration, String name) {
        String expectedEntityId = metadataResolverConfiguration.getExpectedEntityId();
        MetadataHealthCheck metadataHealthCheck = new MetadataHealthCheck(metadataResolver, expectedEntityId);
        environment.healthChecks().register(name, metadataHealthCheck);

        environment.admin().addTask(new Task(name + "-refresh") {
            @Override
            public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
                ((AbstractReloadingMetadataResolver) metadataResolver).refresh();
            }
        });
    }

    private IdaKeyStore getKeystoreFromConfig(SigningKeyPairConfiguration keyPairConfiguration) {
        PrivateKey privateSigningKey = keyPairConfiguration.getPrivateKey();
        X509Certificate signingCertificate = new X509CertificateFactory().createCertificate(keyPairConfiguration.getCert());
        PublicKey publicSigningKey = signingCertificate.getPublicKey();
        KeyPair signingKeyPair = new KeyPair(publicSigningKey, privateSigningKey);

        return new IdaKeyStore(signingCertificate, signingKeyPair, Collections.emptyList());
    }
}
