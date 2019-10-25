package stubidp.saml.hub.hub.domain;

import org.joda.time.DateTime;
import org.opensaml.xmlsec.signature.Signature;
import stubidp.saml.utils.core.domain.IdaSamlResponse;
import stubidp.saml.utils.core.domain.PassthroughAssertion;

import java.net.URI;
import java.util.Optional;

public class InboundResponseFromIdp extends IdaSamlResponse {
    private Optional<PassthroughAssertion> matchingDatasetAssertion;
    private Optional<PassthroughAssertion> authnStatementAssertion;
    private Optional<DateTime> notOnOrAfter;
    private Optional<Signature> signature;
    private IdpIdaStatus status;

    public InboundResponseFromIdp(
            String id,
            String inResponseTo,
            String issuer,
            DateTime issueInstant,
            Optional<DateTime> notOnOrAfter,
            IdpIdaStatus status,
            Optional<Signature> signature,
            Optional<PassthroughAssertion> matchingDatasetAssertion,
            URI destination,
            Optional<PassthroughAssertion> authnStatementAssertion) {
        super(id, issueInstant, inResponseTo, issuer, destination);
        this.notOnOrAfter = notOnOrAfter;
        this.signature = signature;
        this.matchingDatasetAssertion = matchingDatasetAssertion;
        this.authnStatementAssertion = authnStatementAssertion;
        this.status = status;
    }

    public Optional<PassthroughAssertion> getMatchingDatasetAssertion() {
        return matchingDatasetAssertion;
    }

    public Optional<PassthroughAssertion> getAuthnStatementAssertion() {
        return authnStatementAssertion;
    }

    public Optional<Signature> getSignature() {
        return signature;
    }

    public IdpIdaStatus getStatus() {
        return status;
    }

    public Optional<DateTime> getNotOnOrAfter() {
        return notOnOrAfter;
    }
}
