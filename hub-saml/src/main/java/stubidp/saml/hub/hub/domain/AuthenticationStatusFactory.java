package stubidp.saml.hub.hub.domain;

import stubidp.saml.utils.core.domain.IdaStatus;

public interface AuthenticationStatusFactory<T extends Enum, U extends IdaStatus> {
    U create(T status, String message);
}
