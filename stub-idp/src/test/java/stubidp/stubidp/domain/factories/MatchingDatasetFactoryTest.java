package stubidp.stubidp.domain.factories;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;
import stubidp.saml.utils.core.domain.Address;
import stubidp.saml.utils.core.domain.AddressFactory;
import stubidp.saml.utils.core.domain.AuthnContext;
import stubidp.saml.utils.core.domain.Gender;
import stubidp.saml.utils.core.domain.MatchingDataset;
import stubidp.stubidp.domain.DatabaseIdpUser;
import stubidp.stubidp.domain.MatchingDatasetValue;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class MatchingDatasetFactoryTest {

    private static final Address currentAddress = new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), null, true);
    private static final Address previousAddress = new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true);
    public static final DatabaseIdpUser completeUser = new DatabaseIdpUser(
            "idpuser-complete",
            UUID.randomUUID().toString(),
    "bar",
    asList(new MatchingDatasetValue<>("Jack", DateTime.now(), DateTime.now(), true), new MatchingDatasetValue<>("Spud", DateTime.now(), DateTime.now(), true)),
    asList(new MatchingDatasetValue<>("Cornelius", DateTime.now(), DateTime.now(), true), new MatchingDatasetValue<>("Aurelius", DateTime.now(), DateTime.now(), true)),
    asList(new MatchingDatasetValue<>("Bauer", DateTime.now(), DateTime.now(), true), new MatchingDatasetValue<>("Superman", DateTime.now().minusDays(5), DateTime.now().minusDays(3), true)),
    Optional.ofNullable(new MatchingDatasetValue<>(Gender.MALE, DateTime.now(), DateTime.now(), true)),
    asList(new MatchingDatasetValue<>(LocalDate.parse("1984-02-29"), DateTime.now(), DateTime.now(), true), new MatchingDatasetValue<>(LocalDate.parse("1984-03-01"), DateTime.now(), DateTime.now(), true)),
    asList(previousAddress,
            currentAddress),
    AuthnContext.LEVEL_2);

    @Test
    public void shouldSplitAddressesIntoCurrentAndPrevious() {

        final MatchingDataset matchingDataset = MatchingDatasetFactory.create(completeUser);
        assertThat(matchingDataset.getCurrentAddresses().size()).isEqualTo(1);
        assertThat(matchingDataset.getPreviousAddresses().size()).isEqualTo(1);
        assertThat(matchingDataset.getCurrentAddresses().get(0)).isEqualTo(currentAddress);
        assertThat(matchingDataset.getPreviousAddresses().get(0)).isEqualTo(previousAddress);

    }
}
