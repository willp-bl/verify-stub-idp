package stubidp.stubidp.resources.singleidp;


import stubidp.stubidp.domain.DatabaseIdpUser;
import stubidp.stubidp.repositories.Idp;
import stubidp.stubidp.repositories.IdpSession;
import stubidp.stubidp.repositories.IdpSessionRepository;
import stubidp.stubidp.repositories.IdpStubsRepository;
import stubidp.stubidp.views.ErrorMessageType;
import stubidp.stubidp.views.HomePageView;
import stubidp.utils.rest.common.SessionId;
import stubidp.stubidp.Urls;
import stubidp.stubidp.cookies.CookieNames;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Optional;

@Path(Urls.SINGLE_IDP_HOMEPAGE_RESOURCE)
@Produces(MediaType.TEXT_HTML)
public class SingleIdpHomePageResource {

    private final IdpStubsRepository idpStubsRepository;
    private final IdpSessionRepository sessionRepository;

    @Inject
    public SingleIdpHomePageResource(IdpStubsRepository idpStubsRepository,
                                     IdpSessionRepository sessionRepository){

        this.idpStubsRepository = idpStubsRepository;
        this.sessionRepository = sessionRepository;
    }

    @GET
    public Response get(@PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
                        @QueryParam(Urls.ERROR_MESSAGE_PARAM) java.util.Optional<ErrorMessageType> errorMessage,
                        @CookieParam(CookieNames.SESSION_COOKIE_NAME) SessionId sessionCookie) {

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        return Response.ok()
                .entity(new HomePageView(idp.getDisplayName(), idp.getFriendlyId(), errorMessage.orElse(ErrorMessageType.NO_ERROR).getMessage(), idp.getAssetId(), getLoggedInUser(sessionCookie)))
                .build();
    }

    private Optional<DatabaseIdpUser> getLoggedInUser(SessionId sessionCookie) {

        Optional<IdpSession> session = Optional.empty();
        Optional<DatabaseIdpUser> loggedInUser = Optional.empty();

        if(sessionCookie != null) {
            session = sessionRepository.get(sessionCookie);
        }
        if(session.isPresent()) {
            loggedInUser = session.get().getIdpUser();
        }
        return loggedInUser;
    }
}
