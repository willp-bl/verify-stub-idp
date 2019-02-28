package stubidp.stubidp.repositories;

import stubidp.utils.rest.common.SessionId;

public interface EidasSessionRepository extends SessionRepository<EidasSession> {
	SessionId createSession(EidasSession session);
}
