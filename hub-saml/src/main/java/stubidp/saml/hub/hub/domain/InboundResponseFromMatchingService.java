package stubidp.saml.hub.hub.domain;

import java.util.Optional;
import org.joda.time.DateTime;
import stubidp.saml.hub.core.domain.IdaMatchingServiceResponse;
import stubidp.saml.hub.hub.transformers.inbound.MatchingServiceIdaStatus;
import stubidp.saml.utils.core.domain.PassthroughAssertion;

public class InboundResponseFromMatchingService extends IdaMatchingServiceResponse {
    private Optional<PassthroughAssertion> matchingServiceAssertion;
    private MatchingServiceIdaStatus status;

    @SuppressWarnings("unused") // needed for JAXB
    private InboundResponseFromMatchingService() {
    }

    public InboundResponseFromMatchingService(String responseId, String inResponseTo, String issuer, DateTime issueInstant, MatchingServiceIdaStatus status, Optional<PassthroughAssertion> matchingServiceAssertion) {
        super(responseId, inResponseTo, issuer, issueInstant);
        this.matchingServiceAssertion = matchingServiceAssertion;
        this.status = status;
    }

    public Optional<PassthroughAssertion> getMatchingServiceAssertion() {
        return matchingServiceAssertion;
    }

    public MatchingServiceIdaStatus getStatus() {
        return status;
    }
}
