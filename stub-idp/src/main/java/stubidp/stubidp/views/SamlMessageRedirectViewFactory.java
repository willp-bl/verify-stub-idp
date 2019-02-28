package stubidp.stubidp.views;

import com.google.common.net.HttpHeaders;
import stubidp.stubidp.domain.SamlResponse;
import stubidp.stubidp.cookies.CookieNames;
import stubidp.stubidp.cookies.HttpOnlyNewCookie;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

/*
 * Generate the HTML for a SAML Redirect
 */
public abstract class SamlMessageRedirectViewFactory {
    private final SamlMessageType samlMessageType;

    @Inject
    public SamlMessageRedirectViewFactory(SamlMessageType samlMessageType){
        this.samlMessageType = samlMessageType;
    }

    public Response sendSamlMessage(SamlResponse samlResponse) {
        SamlRedirectView samlFormPostingView = getSamlRedirectView(samlResponse.getHubUrl(), samlResponse.getResponseString(), samlResponse.getRelayState(), Optional.empty());
        return Response.ok(samlFormPostingView)
            .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store")
            .header(HttpHeaders.PRAGMA, "no-cache")
            .cookie(new HttpOnlyNewCookie(CookieNames.SESSION_COOKIE_NAME,"","/","",0,false))
            .build();
    }

    private SamlRedirectView getSamlRedirectView(URI targetUri, String samlMessage, String relayState, Optional<Boolean> registration) {
        return new SamlRedirectView(targetUri, samlMessage, samlMessageType, relayState, registration);
    }
}
