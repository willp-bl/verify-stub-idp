package stubidp.stubidp.exceptions.mappers;

import io.prometheus.client.Counter;
import org.apache.log4j.Logger;
import stubidp.stubidp.exceptions.InvalidAuthnRequestException;
import stubidp.stubidp.views.ErrorPageView;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class InvalidAuthnRequestExceptionMapper implements ExceptionMapper<InvalidAuthnRequestException> {

    private static final String STUBIDP_VERIFY_INVALID_AUTHN_REQUESTS_RECEIVED_TOTAL = "stubidp_verify_invalid_AuthnRequests_received_total";
    public static final Counter invalidVerifyAuthnRequests = Counter.build()
            .name(STUBIDP_VERIFY_INVALID_AUTHN_REQUESTS_RECEIVED_TOTAL)
            .help("Number of invalid verify authn requests received.")
            .register();

    private static final Logger LOG = Logger.getLogger(InvalidAuthnRequestException.class);

    @Override
    public Response toResponse(InvalidAuthnRequestException exception) {
        LOG.error(exception);
        invalidVerifyAuthnRequests.inc();
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorPageView())
                .build();
    }
}
