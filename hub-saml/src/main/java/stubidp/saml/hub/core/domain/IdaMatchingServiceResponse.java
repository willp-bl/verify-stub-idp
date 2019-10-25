package stubidp.saml.hub.core.domain;

import org.joda.time.DateTime;
import stubidp.saml.utils.core.domain.IdaMessage;
import stubidp.saml.utils.core.domain.IdaResponse;

public abstract class IdaMatchingServiceResponse extends IdaMessage implements IdaResponse {

    private String inResponseTo;

    protected IdaMatchingServiceResponse() {
    }

    public IdaMatchingServiceResponse(String responseId, String inResponseTo, String issuer, DateTime issueInstant) {
        super(responseId, issuer, issueInstant);
        this.inResponseTo = inResponseTo;
    }

    public String getInResponseTo() {
        return inResponseTo;
    }
}
