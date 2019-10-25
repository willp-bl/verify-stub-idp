package stubidp.saml.hub.core.validators.assertion;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import stubidp.saml.hub.core.DateTimeFreezer;
import stubidp.saml.hub.hub.validators.authnrequest.ConcurrentMapIdExpirationCache;
import stubidp.saml.hub.hub.validators.authnrequest.IdExpirationCache;
import stubidp.saml.hub.core.OpenSAMLRunner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTimeZone.UTC;
import static stubidp.saml.hub.core.errors.SamlTransformationErrorFactory.authnStatementAlreadyReceived;
import static stubidp.saml.hub.core.errors.SamlTransformationErrorFactory.duplicateMatchingDataset;
import static stubidp.saml.utils.core.test.SamlTransformationErrorManagerTestHelper.validateFail;
import static stubidp.saml.utils.core.test.builders.AssertionBuilder.anAssertion;
import static stubidp.saml.utils.core.test.builders.SubjectBuilder.aSubject;
import static stubidp.saml.utils.core.test.builders.SubjectConfirmationBuilder.aSubjectConfirmation;
import static stubidp.saml.utils.core.test.builders.SubjectConfirmationDataBuilder.aSubjectConfirmationData;

public class DuplicateAssertionValidatorTest extends OpenSAMLRunner {

    private ConcurrentMap<String, DateTime> duplicateIds;
    private DuplicateAssertionValidator duplicateAssertionValidator;

    @BeforeEach
    public void setUp() {
        DateTimeFreezer.freezeTime();

        duplicateIds = new ConcurrentHashMap<>();
        duplicateIds.put("duplicate", DateTime.now().plusMinutes(5));
        duplicateIds.put("expired-duplicate", DateTime.now().minusMinutes(2));

        IdExpirationCache<String> idExpirationCache = new ConcurrentMapIdExpirationCache<>(duplicateIds);
        duplicateAssertionValidator = new DuplicateAssertionValidatorImpl(idExpirationCache);
    }

    @Test
    public void validateAuthnStatementAssertion_shouldPassIfTheAssertionIsNotADuplicateOfAPreviousOne() throws Exception {
        Assertion assertion = anAssertion().withId("not-duplicate").buildUnencrypted();
        duplicateAssertionValidator.validateAuthnStatementAssertion(assertion);
    }

    @Test
    public void validateAuthnStatementAssertion_shouldPassIfTwoAssertionsHaveTheSameIdButTheFirstAssertionHasExpired() throws Exception {
        DateTime futureDate = DateTime.now().plusMinutes(6);

        Assertion assertion = createAssertion("expired-duplicate", futureDate);
        duplicateAssertionValidator.validateAuthnStatementAssertion(assertion);

        assertThat(duplicateIds.get("expired-duplicate")).isEqualTo(futureDate.toDateTime(UTC));
    }

    @Test
    public void validateAuthnStatementAssertion_shouldThrowAnExceptionIfTheAssertionIsADuplicateOfAPreviousOne() throws Exception {
        Assertion assertion = anAssertion().withId("duplicate").buildUnencrypted();
        validateFail(
            ()-> duplicateAssertionValidator.validateAuthnStatementAssertion(assertion),
            authnStatementAlreadyReceived("duplicate")
        );
    }

    @Test
    public void validateAuthnStatementAssertion_shouldStoreTheAssertionIdIfNotADuplicate() throws Exception {
        DateTime futureDate = DateTime.now().plusMinutes(6);

        Assertion assertion = createAssertion("new-id", futureDate);
        duplicateAssertionValidator.validateAuthnStatementAssertion(assertion);

        assertThat(duplicateIds.get("new-id")).isEqualTo(futureDate.toDateTime(UTC));
    }

    @Test
    public void validateMatchingDataSetAssertion_shouldPassIfTheAssertionIsNotADuplicateOfAPreviousOne() throws Exception {
        Assertion assertion = anAssertion().withId("not-duplicate").buildUnencrypted();
        duplicateAssertionValidator.validateMatchingDataSetAssertion(assertion, "issuer");
    }

    @Test
    public void validateMatchingDataSetAssertion_shouldPassIfTwoAssertionsHaveTheSameIdButTheFirstAssertionHasExpired() throws Exception {
        DateTime futureDate = DateTime.now().plusMinutes(6);

        Assertion assertion = createAssertion("expired-duplicate", futureDate);
        duplicateAssertionValidator.validateMatchingDataSetAssertion(assertion, "issuer");

        assertThat(duplicateIds.get("expired-duplicate")).isEqualTo(futureDate.toDateTime(UTC));
    }

    @Test
    public void validateMatchingDataSetAssertion_shouldThrowAnExceptionIfTheAssertionIsADuplicateOfAPreviousOne() throws Exception {
        Assertion assertion = anAssertion().withId("duplicate").buildUnencrypted();
        validateFail(
            ()-> duplicateAssertionValidator.validateMatchingDataSetAssertion(assertion, "issuer"),
            duplicateMatchingDataset("duplicate", "issuer")
        );
    }

    @Test
    public void validateMatchingDataSetAssertion_shouldStoreTheAssertionIdIfNotADuplicate() throws Exception {
        DateTime futureDate = DateTime.now().plusMinutes(6);

        Assertion assertion = createAssertion("new-id", futureDate);
        duplicateAssertionValidator.validateMatchingDataSetAssertion(assertion, "issuer");

        assertThat(duplicateIds.get("new-id")).isEqualTo(futureDate.toDateTime(UTC));
    }

    private Assertion createAssertion(String id, DateTime notOnOrAfter) {
        SubjectConfirmationData subjectConfirmationData = aSubjectConfirmationData()
            .withNotOnOrAfter(notOnOrAfter).build();
        SubjectConfirmation subjectConfirmation = aSubjectConfirmation()
            .withSubjectConfirmationData(subjectConfirmationData).build();
        Subject subject = aSubject()
            .withSubjectConfirmation(subjectConfirmation).build();
        return anAssertion()
            .withId(id)
            .withSubject(subject)
            .buildUnencrypted();
    }
}
