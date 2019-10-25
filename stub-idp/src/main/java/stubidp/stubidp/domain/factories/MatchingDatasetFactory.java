package stubidp.stubidp.domain.factories;

import stubidp.saml.utils.core.domain.Address;
import stubidp.saml.utils.core.domain.MatchingDataset;
import stubidp.saml.utils.core.domain.SimpleMdsValue;
import stubidp.saml.utils.core.domain.TransliterableMdsValue;
import stubidp.stubidp.domain.DatabaseIdpUser;
import stubidp.stubidp.domain.MatchingDatasetValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class MatchingDatasetFactory {

    private MatchingDatasetFactory() {
    }

    public static MatchingDataset create(final DatabaseIdpUser user) {
        return new MatchingDataset(
                fromTransliterable(user.getFirstnames()),
                from(user.getMiddleNames()),
                fromTransliterable(user.getSurnames()),
                user.getGender().map(MatchingDatasetFactory::from),
                from(user.getDateOfBirths()),
                getCurrentAddresses(user.getAddresses()),
                getPreviousAddresses(user.getAddresses()),
                user.getPersistentId()
        );
    }

    private static List<TransliterableMdsValue> fromTransliterable(List<MatchingDatasetValue<String>> input) {
        return from(input).stream().map(TransliterableMdsValue::new).collect(Collectors.toList());
    }

    private static <T> List<SimpleMdsValue<T>> from(List<MatchingDatasetValue<T>> input) {
        return input.stream().map(MatchingDatasetFactory::from).collect(Collectors.toList());
    }

    private static <T> SimpleMdsValue<T> from(MatchingDatasetValue<T> input) {
        return new SimpleMdsValue<>(input.getValue(), input.getFrom(), input.getTo(), input.isVerified());
    }

    private static List<Address> getPreviousAddresses(List<Address> addresses) {
        List<Address> previousAddresses = new ArrayList<>();
        for (Address address : addresses) {
            if (address.getTo().isPresent()) {
                previousAddresses.add(address);
            }
        }
        return previousAddresses;
    }

    private static List<Address> getCurrentAddresses(List<Address> addresses) {
        List<Address> currentAddresses = new ArrayList<>();
        for (Address address : addresses) {
            if (!address.getTo().isPresent()) {
                currentAddresses.add(address);
            }
        }
        return currentAddresses;
    }
}
