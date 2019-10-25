package stubidp.saml.hub.hub.domain;

import org.joda.time.DateTime;
import stubidp.saml.utils.core.domain.PersistentId;

import java.net.URI;

public class MatchingServiceHealthCheckRequest extends BaseHubAttributeQueryRequest {

    public MatchingServiceHealthCheckRequest(String id, DateTime issueInstant, PersistentId persistentId, URI assertionConsumerServiceUrl, String authnRequestIssuerEntityId, String hubEntityId) {
        super(id, hubEntityId, issueInstant, null, persistentId, assertionConsumerServiceUrl, authnRequestIssuerEntityId);
    }
}
