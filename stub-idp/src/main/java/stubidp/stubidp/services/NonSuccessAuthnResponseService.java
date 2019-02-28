package stubidp.stubidp.services;

import stubidp.saml.utils.core.domain.IdentityProviderAssertion;
import stubidp.saml.utils.core.domain.IpAddress;
import stubidp.stubidp.domain.DatabaseIdpUser;
import stubidp.stubidp.domain.FraudIndicator;
import stubidp.stubidp.domain.OutboundResponseFromIdp;
import stubidp.stubidp.domain.SamlResponse;
import stubidp.stubidp.domain.SamlResponseFromValue;
import stubidp.stubidp.repositories.Idp;
import stubidp.stubidp.repositories.IdpSession;
import stubidp.stubidp.repositories.IdpStubsRepository;
import stubidp.stubidp.repositories.MetadataRepository;
import stubidp.stubidp.saml.transformers.OutboundResponseFromIdpTransformerProvider;
import stubidp.stubidp.StubIdpModule;
import stubidp.stubidp.domain.factories.AssertionFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;

public class NonSuccessAuthnResponseService {

    private final IdpStubsRepository idpStubsRepository;
    private final MetadataRepository metadataRepository;
    private final AssertionFactory assertionFactory;
    private final OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider;

    @Inject
    public NonSuccessAuthnResponseService(
            IdpStubsRepository idpStubsRepository,
            @Named(StubIdpModule.HUB_METADATA_REPOSITORY) MetadataRepository metadataRepository,
            AssertionFactory assertionFactory,
            OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider) {

        this.idpStubsRepository = idpStubsRepository;
        this.metadataRepository = metadataRepository;
        this.assertionFactory = assertionFactory;
        this.outboundResponseFromIdpTransformerProvider = outboundResponseFromIdpTransformerProvider;
    }

    public SamlResponse generateFraudResponse(String idpName, String samlRequestId, FraudIndicator fraudIndicatorParam, String clientIpAddress, IdpSession session) {
        String requestId = session.getIdaAuthnRequestFromHub().getId();
        DatabaseIdpUser idpUser = IdpUserService.createRandomUser();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();

        IdentityProviderAssertion matchingDatasetAssertion = assertionFactory.createMatchingDatasetAssertion(idp.getIssuerId(), idpUser, requestId);
        IdentityProviderAssertion authnStatementAssertion = assertionFactory.createFraudAuthnStatementAssertion(
                idp.getIssuerId(),
                idpUser,
                requestId,
                idpName,
                fraudIndicatorParam.name(),
                new IpAddress(clientIpAddress));

        OutboundResponseFromIdp successResponseFromIdp = OutboundResponseFromIdp.createSuccessResponseFromIdp(
                samlRequestId,
                idp.getIssuerId(),
                matchingDatasetAssertion,
                authnStatementAssertion,
                hubUrl);

        return generateResponse(idp, successResponseFromIdp, hubUrl, session.getRelayState());
    }

    public SamlResponse generateAuthnPending(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp authnPendingResponseIssuedByIdp = OutboundResponseFromIdp.createAuthnPendingResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, authnPendingResponseIssuedByIdp, hubUrl, relayState);
    }

    public SamlResponse generateUpliftFailed(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp upliftFailedResponseIssuedByIdp = OutboundResponseFromIdp.createUpliftFailedResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, upliftFailedResponseIssuedByIdp, hubUrl, relayState);
    }

    public SamlResponse generateAuthnCancel(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp authnCancelResponseIssuedByIdp = OutboundResponseFromIdp.createAuthnCancelResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, authnCancelResponseIssuedByIdp, hubUrl, relayState);
    }

    public SamlResponse generateNoAuthnContext(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp noAuthnContextResponseIssuedByIdp = OutboundResponseFromIdp.createNoAuthnContextResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, noAuthnContextResponseIssuedByIdp, hubUrl, relayState);
    }

    public SamlResponse generateAuthnFailed(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp failureResponse = OutboundResponseFromIdp.createAuthnFailedResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, failureResponse, hubUrl, relayState);
    }

    public SamlResponse generateRequesterError(String samlRequestId, String requesterErrorMessage, String idpName, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp requesterErrorResponse = OutboundResponseFromIdp.createRequesterErrorResponseIssuedByIdp(
                samlRequestId,
                idp.getIssuerId(),
                hubUrl,
                requesterErrorMessage);
        return generateResponse(idp, requesterErrorResponse, hubUrl, relayState);
    }

    private SamlResponseFromValue<OutboundResponseFromIdp> generateResponse(Idp idp, OutboundResponseFromIdp outboundResponseFromIdp, URI hubUrl, String relayState) {
        return new SamlResponseFromValue<OutboundResponseFromIdp>(outboundResponseFromIdp, outboundResponseFromIdpTransformerProvider.get(idp), relayState, hubUrl);
    }

}
