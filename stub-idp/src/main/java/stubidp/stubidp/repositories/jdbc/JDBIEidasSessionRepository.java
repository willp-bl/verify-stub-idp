package stubidp.stubidp.repositories.jdbc;

import org.jdbi.v3.core.Jdbi;
import stubidp.utils.rest.common.SessionId;
import stubidp.stubidp.repositories.EidasSession;
import stubidp.stubidp.repositories.EidasSessionRepository;
import stubidp.stubidp.repositories.SessionRepositoryBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JDBIEidasSessionRepository extends SessionRepositoryBase<EidasSession> implements EidasSessionRepository {
	@Inject
	public JDBIEidasSessionRepository(Jdbi jdbi) {
		super(EidasSession.class, jdbi);
	}

	public SessionId createSession(EidasSession session) {
		return insertSession(session.getSessionId(), session);
	}
}
