package stubidp.stubidp.repositories.jdbc;

import org.jdbi.v3.core.Jdbi;
import stubidp.utils.rest.common.SessionId;
import stubidp.stubidp.repositories.IdpSession;
import stubidp.stubidp.repositories.IdpSessionRepository;
import stubidp.stubidp.repositories.SessionRepositoryBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JDBIIdpSessionRepository extends SessionRepositoryBase<IdpSession> implements IdpSessionRepository {
	@Inject
	public JDBIIdpSessionRepository(Jdbi jdbi) {
		super(IdpSession.class, jdbi);
	}

	public SessionId createSession(IdpSession session) {
		return insertSession(session.getSessionId(), session);
	}
}
