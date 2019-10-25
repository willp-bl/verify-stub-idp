package stubidp.stubidp.resources.idp;

import com.google.common.base.Strings;
import stubidp.shared.cookies.CookieFactory;
import stubidp.shared.csrf.CSRFCheckProtection;
import stubidp.shared.domain.SamlResponse;
import stubidp.shared.views.SamlMessageRedirectViewFactory;
import stubidp.stubidp.Urls;
import stubidp.stubidp.cookies.StubIdpCookieNames;
import stubidp.stubidp.domain.FraudIndicator;
import stubidp.stubidp.domain.SubmitButtonValue;
import stubidp.stubidp.exceptions.GenericStubIdpException;
import stubidp.stubidp.exceptions.InvalidSessionIdException;
import stubidp.stubidp.exceptions.InvalidUsernameOrPasswordException;
import stubidp.stubidp.filters.SessionCookieValueMustExistAsASession;
import stubidp.stubidp.repositories.Idp;
import stubidp.stubidp.repositories.IdpSession;
import stubidp.stubidp.repositories.IdpSessionRepository;
import stubidp.stubidp.repositories.IdpStubsRepository;
import stubidp.stubidp.repositories.Session;
import stubidp.stubidp.services.IdpUserService;
import stubidp.stubidp.services.NonSuccessAuthnResponseService;
import stubidp.stubidp.views.ErrorMessageType;
import stubidp.stubidp.views.LoginPageView;
import stubidp.utils.rest.common.SessionId;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static java.text.MessageFormat.format;

@Path(Urls.IDP_LOGIN_RESOURCE)
@Produces(MediaType.TEXT_HTML)
@CSRFCheckProtection
public class LoginPageResource {

    private final IdpStubsRepository idpStubsRepository;
    private final NonSuccessAuthnResponseService nonSuccessAuthnResponseService;
    private final SamlMessageRedirectViewFactory samlMessageRedirectViewFactory;
    private final IdpUserService idpUserService;
    private final IdpSessionRepository sessionRepository;
    private final CookieFactory cookieFactory;

    @Inject
    public LoginPageResource(
            IdpStubsRepository idpStubsRepository,
            NonSuccessAuthnResponseService nonSuccessAuthnResponseService,
            SamlMessageRedirectViewFactory samlMessageRedirectViewFactory,
            IdpUserService idpUserService,
            IdpSessionRepository sessionRepository,
            CookieFactory cookieFactory)
    {
        this.nonSuccessAuthnResponseService = nonSuccessAuthnResponseService;
        this.idpStubsRepository = idpStubsRepository;
        this.samlMessageRedirectViewFactory = samlMessageRedirectViewFactory;
        this.idpUserService = idpUserService;
        this.sessionRepository = sessionRepository;
        this.cookieFactory = cookieFactory;
     }

    @GET
    public Response get(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) java.util.Optional<ErrorMessageType> errorMessage,
            @CookieParam(StubIdpCookieNames.SESSION_COOKIE_NAME) SessionId sessionCookie) {

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        if (sessionCookie == null) {
            return createSessionAndShowLoginForm(idp, errorMessage);
        }

        Optional<IdpSession> session = sessionRepository.get(sessionCookie);

        if (sessionContainsUser(session)) {
            if (sessionHasIdaAuthnRequestFromHub(session)) {

                return redirectToConsentPage(idpName);

            } else {

                return redirectToHomePage(idpName);

            }
        } else {

            return showLoginForm(session, idp, errorMessage);

        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam(Urls.USERNAME_PARAM) String username,
            @FormParam(Urls.PASSWORD_PARAM) String password,
            @FormParam(Urls.SUBMIT_PARAM) @NotNull SubmitButtonValue submitButtonValue,
            @CookieParam(StubIdpCookieNames.SESSION_COOKIE_NAME) SessionId sessionCookie) {

        switch (submitButtonValue) {
            case Cancel: {

                Optional<IdpSession> session = sessionRepository.deleteAndGet(sessionCookie);

                if(sessionHasIdaAuthnRequestFromHub(session)) {
                    String samlRequestId = session.get().getIdaAuthnRequestFromHub().getId();
                    final SamlResponse cancelResponse =
                            nonSuccessAuthnResponseService.generateAuthnCancel(
                                                                            idpName,
                                                                            samlRequestId,
                                                                            session.get().getRelayState());

                    return samlMessageRedirectViewFactory.sendSamlResponse(cancelResponse);

                } else {

                    return redirectToHomePage(idpName);

                }
            }

            case SignIn:
                Optional<IdpSession> session = Optional.empty();

                if(sessionCookie == null) {

                    return createSessionAttachUserAndRedirectToHomePage(idpName, username, password, session);
                }

                session = sessionRepository.get(sessionCookie);

                if(sessionHasIdaAuthnRequestFromHub(session)) {

                    return attachUserToSessionAndRedirectToConsent(idpName, username, password, session);
                } else {

                    return createSessionAttachUserAndRedirectToHomePage(idpName, username, password, session);
                }

            default:
                throw new GenericStubIdpException("unknown submit button value", Response.Status.BAD_REQUEST);
        }
    }

    @POST
    @Path(Urls.LOGIN_AUTHN_FAILURE_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SessionCookieValueMustExistAsASession
    public Response postAuthnLoginFailure(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(StubIdpCookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        IdpSession session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse loginFailureResponse = nonSuccessAuthnResponseService.generateAuthnFailed(idpName, session.getIdaAuthnRequestFromHub().getId(), session.getRelayState());
        return samlMessageRedirectViewFactory.sendSamlResponse(loginFailureResponse);
    }

    @POST
    @Path(Urls.LOGIN_NO_AUTHN_CONTEXT_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SessionCookieValueMustExistAsASession
    public Response postNoAuthnContext(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(StubIdpCookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        IdpSession session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse noAuthnResponse = nonSuccessAuthnResponseService.generateNoAuthnContext(idpName, session.getIdaAuthnRequestFromHub().getId(), session.getRelayState());
        return samlMessageRedirectViewFactory.sendSamlResponse(noAuthnResponse);
    }

    @POST
    @Path(Urls.LOGIN_UPLIFT_FAILED_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SessionCookieValueMustExistAsASession
    public Response postUpliftFailed(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(StubIdpCookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        IdpSession session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse upliftFailedResponse = nonSuccessAuthnResponseService.generateUpliftFailed(idpName, session.getIdaAuthnRequestFromHub().getId(), session.getRelayState());
        return samlMessageRedirectViewFactory.sendSamlResponse(upliftFailedResponse);
    }


    @POST
    @Path(Urls.LOGIN_FRAUD_FAILURE_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SessionCookieValueMustExistAsASession
    public Response postLoginFraudAuthnFailure(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam(Urls.LOGIN_FAILURE_STATUS_PARAM) @NotNull FraudIndicator fraudIndicatorParam,
            @Context HttpServletRequest httpServletRequest,
            @CookieParam(StubIdpCookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        final String clientIpAddress = httpServletRequest.getRemoteHost();

        IdpSession session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse fraudResponse = nonSuccessAuthnResponseService.generateFraudResponse(idpName, session.getIdaAuthnRequestFromHub().getId(), fraudIndicatorParam, clientIpAddress, session);
        return samlMessageRedirectViewFactory.sendSamlResponse(fraudResponse);
    }

    @POST
    @Path(Urls.LOGIN_REQUESTER_ERROR_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SessionCookieValueMustExistAsASession
    public Response postRequesterError(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam(Urls.REQUESTER_ERROR_MESSAGE_PARAM) String requesterErrorMessage,
            @CookieParam(StubIdpCookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        IdpSession session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse requesterErrorResponseFromIdp = nonSuccessAuthnResponseService.generateRequesterError(session.getIdaAuthnRequestFromHub().getId(), requesterErrorMessage, idpName, session.getRelayState());
        return samlMessageRedirectViewFactory.sendSamlResponse(requesterErrorResponseFromIdp);
    }

    @POST
    @Path(Urls.LOGIN_AUTHN_PENDING_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SessionCookieValueMustExistAsASession
    public Response postAuthnPending(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(StubIdpCookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        IdpSession session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse pendingResponse = nonSuccessAuthnResponseService.generateAuthnPending(idpName, session.getIdaAuthnRequestFromHub().getId(), session.getRelayState());
        return samlMessageRedirectViewFactory.sendSamlResponse(pendingResponse);
    }


    private boolean sessionContainsUser(Optional<IdpSession> session) {
        return session.isPresent() && session.get().getIdpUser().isPresent();
    }

    private boolean sessionHasIdaAuthnRequestFromHub(Optional<IdpSession> session) {
        return session.isPresent() && session.get().getIdaAuthnRequestFromHub() != null;
    }

    private Response createSessionAndShowLoginForm(Idp idp, Optional<ErrorMessageType> errorMessage){

        final IdpSession session = new IdpSession(new SessionId(UUID.randomUUID().toString()));
        final SessionId sessionId = sessionRepository.createSession(session);
        sessionRepository.updateSession(session.getSessionId(), session.setNewCsrfToken());

        return Response.ok().entity(
                new LoginPageView(
                        idp.getDisplayName(),
                        idp.getFriendlyId(),
                        errorMessage.orElse(ErrorMessageType.NO_ERROR).getMessage(),
                        idp.getAssetId(),
                        session.getCsrfToken()))
                .cookie(cookieFactory.createSessionIdCookie(sessionId))
                .cookie(cookieFactory.createSecureCookieWithSecurelyHashedValue(sessionId))
                .build();
    }

    private Response showLoginForm(Optional<IdpSession> session, Idp idp, Optional<ErrorMessageType> errorMessage) {
        session.ifPresent(idpSession -> sessionRepository.updateSession(idpSession.getSessionId(), idpSession.setNewCsrfToken()));
        return Response.ok().entity(
                new LoginPageView(
                        idp.getDisplayName(),
                        idp.getFriendlyId(),
                        errorMessage.orElse(ErrorMessageType.NO_ERROR).getMessage(),
                        idp.getAssetId(),
                        session.map(Session::getCsrfToken).orElse(null)))
                .build();
    }

    private Response redirectToConsentPage(String idpName) {
        return Response.seeOther(UriBuilder.fromPath(Urls.IDP_CONSENT_RESOURCE)
                .build(idpName)).build();
    }

    private Response redirectToHomePage(String idpName) {
        return Response.seeOther((UriBuilder.fromPath(Urls.SINGLE_IDP_HOMEPAGE_RESOURCE))
                .build(idpName)).build();
    }

    private Response redirectToHomePageWithCookie(String idpName, SessionId sessionId) {
        return Response.seeOther((UriBuilder.fromPath(Urls.SINGLE_IDP_HOMEPAGE_RESOURCE))
                .build(idpName))
                .cookie(cookieFactory.createSessionIdCookie(sessionId))
                .build();
    }

    private IdpSession checkAndGetSession(String idpName, SessionId sessionCookie) {
        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new GenericStubIdpException(format("Unable to locate session cookie for " + idpName), Response.Status.BAD_REQUEST);
        }

        Optional<IdpSession> session = sessionRepository.get(sessionCookie);

        if (session.isEmpty() || session.get().getIdaAuthnRequestFromHub() == null) {
            throw new GenericStubIdpException(format("Session is invalid for " + idpName), Response.Status.BAD_REQUEST);
        }
        return session.get();
    }

    private IdpSession checkAndDeleteAndGetSession(String idpName, SessionId sessionCookie) {
        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new GenericStubIdpException(format("Unable to locate session cookie for " + idpName), Response.Status.BAD_REQUEST);
        }

        Optional<IdpSession> session = sessionRepository.deleteAndGet(sessionCookie);

        if (session.isEmpty() || session.get().getIdaAuthnRequestFromHub() == null) {
            throw new GenericStubIdpException(format("Session is invalid for " + idpName), Response.Status.BAD_REQUEST);
        }
        return session.get();
    }

    private Response createErrorResponse(ErrorMessageType errorMessage, String idpName) {
        URI uri = UriBuilder.fromPath(Urls.IDP_LOGIN_RESOURCE)
                .queryParam(Urls.ERROR_MESSAGE_PARAM, errorMessage)
                .build(idpName);
        return Response.seeOther(uri).build();
    }

    private Response attachUserToSessionAndRedirectToConsent(String idpName, String username, String password, Optional<IdpSession> session) {
        try {
            idpUserService.attachIdpUserToSession(idpName, username, password, session.get().getSessionId());
        } catch (InvalidUsernameOrPasswordException e) {
            return createErrorResponse(ErrorMessageType.INVALID_USERNAME_OR_PASSWORD, idpName);
        } catch (InvalidSessionIdException e) {
            return createErrorResponse(ErrorMessageType.INVALID_SESSION_ID, idpName);
        }
        return redirectToConsentPage(idpName);
    }

    private Response createSessionAttachUserAndRedirectToHomePage(String idpName, String username, String password, Optional<IdpSession> session) {
        final SessionId sessionId;

        if (!session.isPresent()) {
            IdpSession idpSession = new IdpSession(
                    new SessionId(UUID.randomUUID().toString()));
            sessionId = sessionRepository.createSession(idpSession);
        } else {
            sessionId = session.get().getSessionId();
        }
        try {
            idpUserService.attachIdpUserToSession(idpName, username, password, sessionId);
        } catch (InvalidUsernameOrPasswordException e) {
            return createErrorResponse(ErrorMessageType.INVALID_USERNAME_OR_PASSWORD, idpName);
        } catch (InvalidSessionIdException e) {
            return createErrorResponse(ErrorMessageType.INVALID_SESSION_ID, idpName);
        }
        return redirectToHomePageWithCookie(idpName, sessionId);
    }
}
