package stubidp.saml.utils.core.domain;

import org.joda.time.DateTime;

import java.net.URI;
import java.util.List;

public class OutboundResponseFromHub extends IdaSamlResponse {

    private List<String> encryptedAssertions;
    private TransactionIdaStatus status;

    public OutboundResponseFromHub(
            String responseId,
            String inResponseTo,
            String issuer,
            DateTime issueInstant,
            TransactionIdaStatus status,
            List<String> encryptedAssertions,
            URI destination) {

        super(responseId, issueInstant, inResponseTo, issuer, destination);
        this.encryptedAssertions = encryptedAssertions;
        this.status = status;
    }

    public List<String> getEncryptedAssertions() {
        return encryptedAssertions;
    }

    public TransactionIdaStatus getStatus() {
        return status;
    }
}
