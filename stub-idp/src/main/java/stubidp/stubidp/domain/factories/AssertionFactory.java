package stubidp.stubidp.domain.factories;

import org.joda.time.DateTime;
import stubidp.saml.utils.core.domain.FraudAuthnDetails;
import stubidp.saml.utils.core.domain.IdentityProviderAssertion;
import stubidp.saml.utils.core.domain.IpAddress;
import stubidp.saml.utils.core.domain.MatchingDataset;
import stubidp.saml.utils.core.domain.PersistentId;
import stubidp.stubidp.domain.DatabaseIdpUser;

import javax.inject.Inject;

import static stubidp.saml.utils.core.domain.IdentityProviderAuthnStatement.createIdentityProviderFraudAuthnStatement;

public class AssertionFactory {

    private final IdentityProviderAssertionFactory identityProviderAssertionFactory;
    private final AssertionRestrictionsFactory assertionRestrictionsFactory;

    @Inject
    public AssertionFactory(IdentityProviderAssertionFactory identityProviderAssertionFactory, AssertionRestrictionsFactory assertionRestrictionsFactory) {
        this.identityProviderAssertionFactory = identityProviderAssertionFactory;
        this.assertionRestrictionsFactory = assertionRestrictionsFactory;
    }

    private PersistentId createPersistentId(String persistentId) {
        return new PersistentId(persistentId);
    }

    public IdentityProviderAssertion createFraudAuthnStatementAssertion(
            String issuerId,
            DatabaseIdpUser user,
            String inResponseToId,
            String idpName,
            String indicator,
            IpAddress userIpAddress) {

        return identityProviderAssertionFactory.createAuthnStatementAssertion(
                createPersistentId(user.getPersistentId()),
                issuerId,
                createIdentityProviderFraudAuthnStatement(new FraudAuthnDetails(idpName + DateTime.now().toString() + inResponseToId, indicator), userIpAddress),
                assertionRestrictionsFactory.createRestrictionsForSendingToHub(inResponseToId)
        );
    }

    public IdentityProviderAssertion createMatchingDatasetAssertion(String issuerId, DatabaseIdpUser user, String requestId) {
        MatchingDataset matchingDataset = MatchingDatasetFactory.create(user);
        return identityProviderAssertionFactory.createMatchingDatasetAssertion(
            createPersistentId(user.getPersistentId()),
            issuerId,
            matchingDataset,
            assertionRestrictionsFactory.createRestrictionsForSendingToHub(requestId));
    }
}
