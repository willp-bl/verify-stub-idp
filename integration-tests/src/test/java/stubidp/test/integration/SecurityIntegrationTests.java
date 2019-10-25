package stubidp.test.integration;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import stubidp.stubidp.Urls;
import stubidp.stubidp.cookies.StubIdpCookieNames;
import stubidp.stubidp.filters.SecurityHeadersFilterTest;
import stubidp.test.integration.steps.AuthnRequestSteps;
import stubidp.test.integration.support.IdpAuthnRequestBuilder;
import stubidp.test.integration.support.IntegrationTestHelper;
import stubidp.test.integration.support.StubIdpAppExtension;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static stubidp.shared.csrf.AbstractCSRFCheckProtectionFilter.CSRF_PROTECT_FORM_KEY;
import static stubidp.stubidp.builders.StubIdpBuilder.aStubIdp;

@ExtendWith(DropwizardExtensionsSupport.class)
public class SecurityIntegrationTests extends IntegrationTestHelper {

    private static final String IDP_NAME = "stub-idp-one";
    private static final String DISPLAY_NAME = "User Login Identity Service";
    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);
    private final AuthnRequestSteps authnRequestSteps = new AuthnRequestSteps(
            client,
            IDP_NAME,
            applicationRule.getLocalPort());

    public static final StubIdpAppExtension applicationRule = new StubIdpAppExtension()
            .withStubIdp(aStubIdp().withId(IDP_NAME).withDisplayName(DISPLAY_NAME).build());

    @BeforeEach
    void refreshMetadata() {
        client.target("http://localhost:"+applicationRule.getAdminPort()+"/tasks/connector-metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    void securityHeaderTest() {
        final Response response = client.target(UriBuilder.fromUri("http://localhost:" + applicationRule.getLocalPort())
                .path("/page_does_not_exist")
                .build())
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
        SecurityHeadersFilterTest.checkSecurityHeaders(response.getHeaders());
    }

    @Test
    void csrfTokenIsUniquePerPageLoad() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        String csrfValueOne = getLoginPageCsrfValue(cookies);
        String csrfValueTwo = getLoginPageCsrfValue(cookies);
        assertThat(csrfValueOne).isNotEqualTo(csrfValueTwo);
    }

    @Test
    void whenCsrfTokenIsModifiedThenRequestDoesNotWork() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        getLoginPageCsrfValue(cookies);

        Form form = new Form();
        form.param(Urls.USERNAME_PARAM, IDP_NAME);
        form.param(Urls.PASSWORD_PARAM, "bar");
        form.param(Urls.SUBMIT_PARAM, "SignIn");
        form.param(CSRF_PROTECT_FORM_KEY, "this_is_not_a_csrf_value");

        Response response = client.target(authnRequestSteps.getStubIdpUri(Urls.IDP_LOGIN_RESOURCE))
                .request()
                .cookie(StubIdpCookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(StubIdpCookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .post(Entity.form(form));

        assertThat(response.getStatus()).isEqualTo(500);
    }

    @Test
    void whenSecureCookieIsModifiedThenRequestDoesNotWork() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();

        Response response = client.target(authnRequestSteps.getStubIdpUri(Urls.IDP_LOGIN_RESOURCE))
                .request()
                .cookie(StubIdpCookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(StubIdpCookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        response = client.target(authnRequestSteps.getStubIdpUri(Urls.IDP_LOGIN_RESOURCE))
                .request()
                .cookie(StubIdpCookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(StubIdpCookieNames.SECURE_COOKIE_NAME, "try this")
                .get();

        assertThat(response.getStatus()).isEqualTo(500);
    }

    @Test
    void whenSameAuthnRequestIsSentTwiceItFails() {
        final String authnRequest = IdpAuthnRequestBuilder.anAuthnRequest()
                .withDestination(UriBuilder.fromUri(authnRequestSteps.getStubIdpUri(Urls.IDP_SAML2_SSO_RESOURCE)).build().toASCIIString())
                .build();

        Response response = authnRequestSteps.postAuthnRequest(List.of(), Optional.empty(), Optional.empty(), authnRequest, Urls.IDP_SAML2_SSO_RESOURCE);
        assertThat(response.getStatus()).isEqualTo(303);

        response = authnRequestSteps.postAuthnRequest(List.of(), Optional.empty(), Optional.empty(), authnRequest, Urls.IDP_SAML2_SSO_RESOURCE);
        assertThat(response.getStatus()).isEqualTo(500);
    }

    private String getLoginPageCsrfValue(AuthnRequestSteps.Cookies cookies) {
        Response response = client.target(authnRequestSteps.getStubIdpUri(Urls.IDP_LOGIN_RESOURCE))
                .request()
                .cookie(StubIdpCookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(StubIdpCookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        final Document entity = Jsoup.parse(response.readEntity(String.class));
        final Element csrfElement = entity.getElementById(CSRF_PROTECT_FORM_KEY);
        if(!Objects.isNull(csrfElement)) {
            return csrfElement.val();
        }
        return null;
    }

}
