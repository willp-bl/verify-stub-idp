package stubidp.stubidp.services;

import stubidp.saml.utils.core.domain.IdentityProviderAssertion;
import stubidp.saml.utils.core.domain.IpAddress;
import stubidp.saml.utils.core.domain.PersistentId;
import stubidp.stubidp.domain.DatabaseIdpUser;
import stubidp.stubidp.domain.OutboundResponseFromIdp;
import stubidp.stubidp.domain.SamlResponseFromValue;
import stubidp.stubidp.domain.factories.IdentityProviderAssertionFactory;
import stubidp.stubidp.repositories.Idp;
import stubidp.stubidp.repositories.IdpSession;
import stubidp.stubidp.repositories.IdpStubsRepository;
import stubidp.stubidp.repositories.MetadataRepository;
import stubidp.stubidp.saml.transformers.OutboundResponseFromIdpTransformerProvider;
import stubidp.stubidp.StubIdpBinder;
import stubidp.stubidp.domain.factories.AssertionRestrictionsFactory;
import stubidp.stubidp.domain.factories.MatchingDatasetFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.UUID;

import static stubidp.saml.utils.core.domain.IdentityProviderAuthnStatement.createIdentityProviderAuthnStatement;

public class SuccessAuthnResponseService {

    private final IdentityProviderAssertionFactory identityProviderAssertionFactory;
    private final IdpStubsRepository idpStubsRepository;
    private final MetadataRepository metadataProvider;
    private final AssertionRestrictionsFactory assertionRestrictionsFactory;
    private final OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider;

    @Inject
    public SuccessAuthnResponseService(
            IdentityProviderAssertionFactory identityProviderAssertionFactory,
            IdpStubsRepository idpStubsRepository,
            @Named(StubIdpBinder.HUB_METADATA_REPOSITORY) MetadataRepository metadataProvider,
            AssertionRestrictionsFactory assertionRestrictionsFactory,
            OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider) {

        this.identityProviderAssertionFactory = identityProviderAssertionFactory;
        this.idpStubsRepository = idpStubsRepository;
        this.metadataProvider = metadataProvider;
        this.assertionRestrictionsFactory = assertionRestrictionsFactory;
        this.outboundResponseFromIdpTransformerProvider = outboundResponseFromIdpTransformerProvider;
    }

    public SamlResponseFromValue<OutboundResponseFromIdp> getSuccessResponse(boolean randomisePid, String remoteIpAddress, String idpName, IdpSession session) {
        URI hubUrl = metadataProvider.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        DatabaseIdpUser idpUser = session.getIdpUser().get();
        String requestId = session.getIdaAuthnRequestFromHub().getId();

        PersistentId persistentId = new PersistentId(idpUser.getPersistentId());
        if(randomisePid) {
            persistentId = new PersistentId(UUID.randomUUID().toString());
        }

        IdentityProviderAssertion matchingDatasetAssertion = identityProviderAssertionFactory.createMatchingDatasetAssertion(
                persistentId,
                idp.getIssuerId(),
                MatchingDatasetFactory.create(idpUser),
                assertionRestrictionsFactory.createRestrictionsForSendingToHub(requestId));

        IdentityProviderAssertion authnStatementAssertion = identityProviderAssertionFactory.createAuthnStatementAssertion(
                persistentId,
                idp.getIssuerId(),
                createIdentityProviderAuthnStatement(idpUser.getLevelOfAssurance(), new IpAddress(remoteIpAddress)),
                assertionRestrictionsFactory.createRestrictionsForSendingToHub(requestId));

        OutboundResponseFromIdp idaResponse = OutboundResponseFromIdp.createSuccessResponseFromIdp(
                session.getIdaAuthnRequestFromHub().getId(),
                idp.getIssuerId(),
                matchingDatasetAssertion,
                authnStatementAssertion,
                hubUrl);

        return new SamlResponseFromValue<OutboundResponseFromIdp>(idaResponse, outboundResponseFromIdpTransformerProvider.get(idp), session.getRelayState(), hubUrl);
    }

}