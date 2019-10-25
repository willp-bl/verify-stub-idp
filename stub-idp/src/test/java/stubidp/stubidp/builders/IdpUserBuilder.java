package stubidp.stubidp.builders;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import stubidp.saml.utils.core.domain.Address;
import stubidp.saml.utils.core.domain.AuthnContext;
import stubidp.saml.utils.core.domain.Gender;
import stubidp.stubidp.domain.DatabaseIdpUser;
import stubidp.stubidp.domain.MatchingDatasetValue;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static stubidp.saml.utils.core.domain.AuthnContext.LEVEL_1;

public class IdpUserBuilder {

    private String username = "default-username";
    private String persistentId = "default-persistent-id";
    private String password = "default-password";
    private List<MatchingDatasetValue<String>> firstnames = singletonList(new MatchingDatasetValue<String>(
        "default-first-name",
        DateTime.now().minusYears(20),
        null,
        true
    ));
    private List<MatchingDatasetValue<String>> middleNames = singletonList(new MatchingDatasetValue<String>(
        "default-middle-name",
        DateTime.now().minusYears(20),
        null,
        true
    ));
    private List<MatchingDatasetValue<String>> surnames = singletonList(new MatchingDatasetValue<String>(
        "default-surname",
        DateTime.now().minusYears(20),
        null,
        true
    ));
    private Optional<MatchingDatasetValue<Gender>> gender = Optional.empty();
    private List<MatchingDatasetValue<LocalDate>> dateOfBirths = singletonList(new MatchingDatasetValue<LocalDate>(
        LocalDate.now().minusYears(20),
        DateTime.now().minusYears(20),
        null,
        true
    ));
    private List<Address> addresses = emptyList();
    private AuthnContext levelOfAssurance = LEVEL_1;

    public static IdpUserBuilder anIdpUser() {
        return new IdpUserBuilder();
    }

    public static DatabaseIdpUser anyIdpUser() {
        return anIdpUser().build();
    }

    public DatabaseIdpUser build() {
        return new DatabaseIdpUser(
            username,
            persistentId,
            password,
            firstnames,
            middleNames,
            surnames,
            gender,
            dateOfBirths,
            addresses,
            levelOfAssurance
        );
    }

    public IdpUserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public IdpUserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }
}
