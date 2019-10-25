package stubidp.stubidp.builders;

import org.joda.time.DateTime;
import stubidp.stubidp.domain.MatchingDatasetValue;

public class SimpleMdsValueBuilder<T> {

    private T value = null;

    private DateTime from = DateTime.now().minusDays(5);
    private DateTime to = DateTime.now().plusDays(5);
    private boolean verified = false;

    public static <T> SimpleMdsValueBuilder<T> aSimpleMdsValue() {
        return new SimpleMdsValueBuilder<>();
    }

    public MatchingDatasetValue<T> build() {
        return new MatchingDatasetValue<>(value, from, to, verified);
    }

    public SimpleMdsValueBuilder<T> withValue(T value) {
        this.value = value;
        return this;
    }

    public SimpleMdsValueBuilder<T> withFrom(DateTime from) {
        this.from = from;
        return this;
    }

    public SimpleMdsValueBuilder<T> withTo(DateTime to) {
        this.to = to;
        return this;
    }

    public SimpleMdsValueBuilder<T> withVerifiedStatus(boolean verified) {
        this.verified = verified;
        return this;
    }
}
