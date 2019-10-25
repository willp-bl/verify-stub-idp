package stubidp.stubidp.repositories;

import stubidp.utils.rest.common.SessionId;

public interface IdpSessionRepository extends SessionRepository<IdpSession> {
	SessionId createSession(IdpSession session);
}
