package uk.gov.ida.stub.idp.exceptions;

import com.google.common.base.Optional;
import uk.gov.ida.common.SessionId;

public class InvalidSecureCookieException extends RuntimeException {
    private Optional<SessionId> sessionId;
    private final String message;

    public InvalidSecureCookieException(String message, SessionId sessionId) {
        super(message);
        this.message = message;
        this.sessionId = Optional.fromNullable(sessionId);
    }

    public Optional<SessionId> getSessionId() {
        return sessionId;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
