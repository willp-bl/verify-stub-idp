package stubidp.saml.utils.core.domain;

import org.joda.time.DateTime;

public interface IdaResponse {

    String getId();
    String getInResponseTo();
    DateTime getIssueInstant();
    String getIssuer();
}
