package stubidp.stubidp.repositories.jdbc;

import org.jdbi.v3.core.Jdbi;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stubidp.saml.utils.core.domain.Gender;
import stubidp.stubidp.domain.EidasAddress;
import stubidp.stubidp.domain.EidasAuthnRequest;
import stubidp.stubidp.domain.EidasUser;
import stubidp.stubidp.domain.IdpHint;
import stubidp.stubidp.domain.IdpLanguageHint;
import stubidp.stubidp.repositories.EidasSession;
import stubidp.stubidp.repositories.jdbc.migrations.DatabaseMigrationRunner;
import stubidp.utils.rest.common.SessionId;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class JDBIEidasSessionRepositoryTest {

	private Jdbi jdbi;
	private JDBIEidasSessionRepository repository;
	private final String DATABASE_URL = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";

	@BeforeEach
	void setUp() {
		new DatabaseMigrationRunner().runMigration(DATABASE_URL);

		jdbi = Jdbi.create(DATABASE_URL);
		repository = new JDBIEidasSessionRepository(jdbi);
	}
	
	@Test
	void createSession_shouldCreateEidasSessionAndStoreInDatabase() {
		EidasAuthnRequest authnRequest = new EidasAuthnRequest("7cb0ba32-4ebd-4291-8901-c647d4687572", "test-issuer", "", "", Arrays.asList());

		SessionId eidasSessionId = SessionId.createNewSessionId();
		EidasSession session = new EidasSession(eidasSessionId, authnRequest, "test-relay-state", Arrays.asList(IdpHint.has_ukphotolicence), Arrays.asList("invalid hint"), Optional.of(IdpLanguageHint.cy), Optional.of(true), null, true);
		session.setEidasUser(new EidasUser("Joe", Optional.empty(), "Bloggs", Optional.empty(), "persistentId", new LocalDate(1524655440000L, DateTimeZone.UTC), Optional.of(new EidasAddress("PO Box 123", "", "", "", "", "", "", "", "AB1 2YZ")), Optional.of(Gender.MALE)));
		repository.createSession(session);
		
		String expectedSerializedSession = "{{\"sessionId\":\""+ eidasSessionId.getSessionId() +"\",\"eidasAuthnRequest\":{\"requestId\":\"7cb0ba32-4ebd-4291-8901-c647d4687572\",\"issuer\":\"test-issuer\",\"destination\":\"\",\"requestedLoa\":\"\",\"attributes\":[]},\"relayState\":\"test-relay-state\",\"validHints\":[\"has_ukphotolicence\"],\"invalidHints\":[\"invalid hint\"],\"languageHint\":\"cy\",\"registration\":true,\"csrfToken\":null,\"eidasUser\":{\"firstName\":\"Joe\",\"familyName\":\"Bloggs\",\"persistentId\":\"persistentId\",\"dateOfBirth\":[2018,4,25],\"address\":{\"poBox\":\"PO Box 123\",\"locatorDesignator\":\"\",\"locatorName\":\"\",\"cvAddressArea\":\"\",\"thoroughfare\":\"\",\"postName\":\"\",\"adminunitFirstLine\":\"\",\"adminunitSecondLine\":\"\",\"postCode\":\"AB1 2YZ\"},\"gender\":\"MALE\"},\"signAssertions\":true}}";

		jdbi.useHandle(handle -> {
			Optional<String> result = handle.select("select session_data from stub_idp_session where session_id = ?", eidasSessionId.toString())
					.mapTo(String.class)
					.findFirst();

			assertThat(result.isPresent()).isEqualTo(true);
			assertThat(result.get()).isEqualTo(expectedSerializedSession);
		});
	}

	@Test
	void get_shouldReturnPopulatedEidasSession_whenSessionExists() {
		EidasAuthnRequest authnRequest = new EidasAuthnRequest("7cb0ba32-4ebd-4291-8901-c647d4687572", "test-issuer", "", "", Arrays.asList());

		EidasSession expectedSession = new EidasSession(SessionId.createNewSessionId(), authnRequest, "test-relay-state", Arrays.asList(IdpHint.has_ukphotolicence), Arrays.asList("invalid hint"), Optional.of(IdpLanguageHint.cy), Optional.of(true), null, true);
		expectedSession.setEidasUser(new EidasUser("Joe", Optional.empty(), "Bloggs", Optional.empty(), "persistentId", new LocalDate(1524655440000L, DateTimeZone.UTC), Optional.of(new EidasAddress("PO Box 123", "", "", "", "", "", "", "", "AB1 2YZ")), Optional.of(Gender.MALE)));
		SessionId insertedSessionId = repository.createSession(expectedSession);

		Optional<EidasSession> actualSession = repository.get(insertedSessionId);

		assertThat(actualSession.isPresent()).isEqualTo(true);
		assertThat(actualSession.get()).isInstanceOf(EidasSession.class);
		assertThat(actualSession.get()).usingRecursiveComparison().isEqualTo(expectedSession);
	}
}
