package stubidp.test.integration;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import stubidp.saml.hub.hub.domain.InboundResponseFromIdp;
import stubidp.stubidp.Urls;
import stubidp.stubidp.cookies.StubIdpCookieNames;
import stubidp.test.integration.steps.AuthnRequestSteps;
import stubidp.test.integration.support.IntegrationTestHelper;
import stubidp.test.integration.support.SamlDecrypter;
import stubidp.test.integration.support.StubIdpAppExtension;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static stubidp.metrics.prometheus.bundle.PrometheusBundle.PROMETHEUS_METRICS_RESOURCE;
import static stubidp.stubidp.builders.StubIdpBuilder.aStubIdp;
import static stubidp.utils.rest.common.HttpHeaders.CACHE_CONTROL_KEY;
import static stubidp.utils.rest.common.HttpHeaders.PRAGMA_KEY;

@ExtendWith(DropwizardExtensionsSupport.class)
public class UserLogsInIntegrationTests extends IntegrationTestHelper {

    // Use stub-idp-one as it allows us to use the defaultMetadata in MetadataFactory
    private static final String IDP_NAME = "stub-idp-one";
    private static final String DISPLAY_NAME = "User Login Identity Service";

    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);
    private final AuthnRequestSteps authnRequestSteps = new AuthnRequestSteps(
            client,
            IDP_NAME,
            applicationRule.getLocalPort());
    private static final boolean checkKeyInfo = true;
    private final SamlDecrypter samlDecrypter = new SamlDecrypter(client,
            applicationRule.getVerifyMetadataPath(),
            applicationRule.getConfiguration().getHubEntityId(),
            applicationRule.getLocalPort(),
            empty(),
            applicationRule.getAssertionConsumerServices(),
            checkKeyInfo);

    public static final StubIdpAppExtension applicationRule = new StubIdpAppExtension(Map.ofEntries(Map.entry("isPrometheusEnabled", "true")))
            .withStubIdp(aStubIdp()
                    .withId(IDP_NAME)
                    .withDisplayName(DISPLAY_NAME)
                    .sendKeyInfo(checkKeyInfo)
                    .build());

    @BeforeEach
    public void refreshMetadata() {
        client.target("http://localhost:"+applicationRule.getAdminPort()+"/tasks/metadata-refresh").request().post(Entity.text(""));
    }

//    @Test
//    @Order(Integer.MAX_VALUE)
    public void zzz_checkMetrics() {
        Response response = client.target(UriBuilder.fromUri("http://localhost:" + applicationRule.getAdminPort())
                .path(PROMETHEUS_METRICS_RESOURCE)
                .build()).request().get();
        assertThat(response.getStatus()).isEqualTo(200);
        final String entity = response.readEntity(String.class);
        final List<String> metrics = List.of(entity.split(System.lineSeparator())).stream().filter(s -> s.startsWith("stubidp_")).collect(Collectors.toList());
        metricsContains(metrics, "stubidp_verify_receivedAuthnRequests_total 6.0");
//        metricsContains(metrics, "stubidp_eidas_receivedAuthnRequests_total");
        metricsContains(metrics, "stubidp_verify_successfulAuthnRequests_total 5.0");
        metricsContains(metrics, "stubidp_eidas_successfulAuthnRequests_total 0.0");
        metricsContains(metrics, "stubidp_verify_sentAuthnResponses_success_total 3.0");
//        metricsContains(metrics, "stubidp_eidas_sentAuthnResponses_success_total 0.0");
        metricsContains(metrics, "stubidp_verify_invalid_AuthnRequests_received_total 1.0");
        metricsContains(metrics, "stubidp_eidas_invalid_AuthnRequests_received_total 0.0");
        metricsContains(metrics, "stubidp_verify_sentAuthnResponses_failure_total{failure_type=\"fraud\",} 1.0");
        metricsContains(metrics, "stubidp_db_users_total 12.0");
        metricsContains(metrics, "stubidp_db_sessions_total 1.0");
        metricsContains(metrics, "stubidp_replay_cache_total 5.0");
    }

    private void metricsContains(List<String> metrics, String metric) {
        assertThat(metrics.stream().anyMatch(m -> m.startsWith(metric))).withFailMessage(format("{0} not in {1}", metric, metrics)).isTrue();
    }

    @Test
    @Order(1)
    public void incorrectlySignedAuthnRequestFailsTest() {
        Response response = authnRequestSteps.userPostsAuthnRequestToStubIdpReturnResponse(List.of(), empty(), empty(), true);
        assertThat(response.getStatus()).isEqualTo(500);
    }

    @Test
    @Order(1)
    public void loginBehaviourTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        authnRequestSteps.userLogsIn(cookies);
        authnRequestSteps.userConsentsReturnSamlResponse(cookies, false);
    }

    @Test
    @Order(1)
    public void failureBehaviourTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        final String response = authnRequestSteps.userFailureFraud(cookies);

        zzz_checkMetrics();
    }

    @Test
    @Order(1)
    @Disabled
    public void testStaleSessionReaper() throws InterruptedException {
        // set times in StaleSessionReaperConfiguration to 1s, run this test and check the log lines
        for(int i=0;i<10;i++) {
            authnRequestSteps.userPostsAuthnRequestToStubIdp();
            Thread.sleep(1000);
        }
    }

    @Test
    @Order(1)
    public void debugPageLoadsTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        authnRequestSteps.userLogsIn(cookies);
        authnRequestSteps.userViewsTheDebugPage(cookies);
    }

    @Test
    @Order(1)
    public void ensureImagesAreCacheableTest() {
        Response response = client.target(authnRequestSteps.getStubIdpUri("/assets/images/providers/stub-idp-demo-one.png"))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        // ensure data can be stored by browser
        assertThat(response.getHeaderString(CACHE_CONTROL_KEY)).isNull();
        assertThat(response.getHeaderString(PRAGMA_KEY)).isNull();
    }

    @Test
    @Order(1)
    public void idpNotFoundTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        Response response = client.target(authnRequestSteps.getStubIdpUri(UriBuilder.fromPath(Urls.IDP_LOGIN_RESOURCE).build("idp_that_does_not_exist").toString()))
                .request()
                .cookie(StubIdpCookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(StubIdpCookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .get();
        assertThat(response.getStatus()).isEqualTo(404);
        final String body = response.readEntity(String.class);
        assertThat(body).contains("No idp found with friendlyId: idp_that_does_not_exist");
    }

    @Test
    @Order(1)
    public void randomizedPidTest() throws IOException, ResolverException, CertificateException {
        final AuthnRequestSteps.Cookies cookies1 = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        authnRequestSteps.userLogsIn(cookies1);
        final String samlResponse = authnRequestSteps.userConsentsReturnSamlResponse(cookies1, false);
        final InboundResponseFromIdp inboundResponseFromIdp = samlDecrypter.decryptSaml(samlResponse);
        final String firstPid = inboundResponseFromIdp.getAuthnStatementAssertion().get().getPersistentId().getNameId();

        final AuthnRequestSteps.Cookies cookies2 = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        authnRequestSteps.userLogsIn(cookies2);
        final String samlResponse2 = authnRequestSteps.userConsentsReturnSamlResponse(cookies2, true);
        final InboundResponseFromIdp inboundResponseFromIdp2 = samlDecrypter.decryptSaml(samlResponse2);
        assertThat(inboundResponseFromIdp2.getAuthnStatementAssertion().get().getPersistentId().getNameId()).isNotEqualTo(firstPid);
    }

}
