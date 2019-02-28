package stubidp.stubidp.resources;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import stubidp.utils.rest.common.SessionId;
import stubidp.saml.utils.hub.domain.IdaAuthnRequestFromHub;
import stubidp.stubidp.repositories.Idp;
import stubidp.stubidp.repositories.IdpSession;
import stubidp.stubidp.repositories.IdpSessionRepository;
import stubidp.stubidp.repositories.IdpStubsRepository;
import stubidp.stubidp.resources.idp.DebugPageResource;
import stubidp.stubidp.views.DebugPageView;

import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DebugPageResourceTest {

    private final String IDP_NAME = "an idp name";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();
    private final String RELAY_STATE = "relayState";

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private DebugPageResource resource;
    private Idp idp = new Idp(IDP_NAME,IDP_NAME,"an assetId", false, null, null);

    @Mock
    private IdpStubsRepository idpStubsRepository;
    @Mock
    private IdpSessionRepository sessionRepository;
    @Mock
    private IdaAuthnRequestFromHub idaAuthnRequestFromHub;


    @Before
    public void createResource() {
        resource = new DebugPageResource(
                idpStubsRepository,
                sessionRepository
        );
        when(idpStubsRepository.getIdpWithFriendlyId(IDP_NAME)).thenReturn(idp);
    }

    @Test
    public void shouldHaveNullJourneyIdInPageViewWhenNoIdReceived() {
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null, Optional.empty(), null)));

        Response response = resource.get(IDP_NAME, SESSION_ID);

        assertThat(response.getEntity()).isInstanceOf(DebugPageView.class);
        assertThat(((DebugPageView)response.getEntity()).getSingleIdpJourneyId()).isNull();
    }

    @Test
    public void shouldHaveJourneyIdInPageViewWhenIdReceived() {
        UUID uuid = UUID.randomUUID();
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null, Optional.ofNullable(uuid), null)));

        Response response = resource.get(IDP_NAME, SESSION_ID);

        assertThat(response.getEntity()).isInstanceOf(DebugPageView.class);
        assertThat(((DebugPageView)response.getEntity()).getSingleIdpJourneyId()).isEqualTo(uuid);
    }

}
