package stubidp.saml.hub.test.outbound;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import stubidp.saml.hub.core.domain.IdaMatchingServiceResponse;

public class HealthCheckResponseFromMatchingService extends IdaMatchingServiceResponse {
    public HealthCheckResponseFromMatchingService(String entityId, String healthCheckReqeustId) {
        super("healthcheck-response-id", healthCheckReqeustId, entityId, DateTime.now());
    }

    public HealthCheckResponseFromMatchingService(final String responseId,
                                                  final String entityId,
                                                  final String healthCheckReqeustId) {
        super(responseId, healthCheckReqeustId, entityId, DateTime.now(DateTimeZone.UTC));
    }
}

