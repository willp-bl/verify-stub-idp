package stubidp.stubidp.repositories;

import org.joda.time.Duration;
import stubidp.utils.rest.common.SessionId;

import java.util.Optional;

interface SessionRepository<T extends Session> {
	boolean containsSession(SessionId sessionToken);
	Optional<T> get(SessionId sessionToken);
	SessionId updateSession(SessionId sessionToken, Session session);
	void deleteSession(SessionId sessionToken);
	Optional<T> deleteAndGet(SessionId sessionToken);
	long countSessionsOlderThan(Duration duration);
	void deleteSessionsOlderThan(Duration duration);
	long countSessionsInDatabase();
}
