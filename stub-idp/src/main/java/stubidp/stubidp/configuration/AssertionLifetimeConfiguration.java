package stubidp.stubidp.configuration;

import io.dropwizard.util.Duration;

public interface AssertionLifetimeConfiguration {
    Duration getAssertionLifetime();
}
