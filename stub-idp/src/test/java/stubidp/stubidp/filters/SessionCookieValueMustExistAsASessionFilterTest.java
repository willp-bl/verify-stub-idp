package stubidp.stubidp.filters;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import stubidp.stubidp.cookies.HmacValidator;
import stubidp.stubidp.exceptions.InvalidSecureCookieException;
import stubidp.stubidp.exceptions.SecureCookieNotFoundException;
import stubidp.stubidp.exceptions.SessionIdCookieNotFoundException;
import stubidp.stubidp.exceptions.SessionNotFoundException;
import stubidp.stubidp.repositories.EidasSessionRepository;
import stubidp.stubidp.repositories.IdpSessionRepository;
import stubidp.utils.rest.common.SessionId;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static stubidp.stubidp.cookies.CookieNames.SECURE_COOKIE_NAME;
import static stubidp.stubidp.cookies.CookieNames.SESSION_COOKIE_NAME;
import static stubidp.stubidp.filters.SessionCookieValueMustExistAsASessionFilter.NO_CURRENT_SESSION_COOKIE_VALUE;

@RunWith(MockitoJUnitRunner.class)
public class SessionCookieValueMustExistAsASessionFilterTest {

    private boolean isSecureCookieEnabled = true;
    @Mock
    private HmacValidator hmacValidator;
    @Mock
    private IdpSessionRepository idpSessionRepository;
    @Mock
    private EidasSessionRepository eidasSessionRepository;
    @Mock
    private ContainerRequestContext containerRequestContext;

    @Test (expected = SessionIdCookieNotFoundException.class)
    public void shouldReturnNullWhenCheckingNotRequiredButNoCookies() throws Exception {
        Map<String, Cookie> cookies = ImmutableMap.of();
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test (expected = SecureCookieNotFoundException.class)
    public void shouldReturnNullWhenCheckingNotRequiredButSecureCookie() throws Exception {
        Map<String, Cookie> cookies = ImmutableMap.of(SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, "some-session-id"));
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test (expected = InvalidSecureCookieException.class)
    public void shouldReturnNullWhenCheckingNotRequiredButSessionCookieIsSetToNoCurrentValue() throws Exception {
        Map<String, Cookie> cookies = ImmutableMap.of(SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, "some-session-id"), SECURE_COOKIE_NAME, new NewCookie(SECURE_COOKIE_NAME, NO_CURRENT_SESSION_COOKIE_VALUE));
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test (expected = InvalidSecureCookieException.class)
    public void shouldReturnNullWhenCheckingNotRequiredButSessionCookieAndSecureCookieDontMatchUp() throws Exception {
        SessionId sessionId = SessionId.createNewSessionId();
        Map<String, Cookie> cookies = ImmutableMap.of(SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, sessionId.toString()), SECURE_COOKIE_NAME, new NewCookie(SECURE_COOKIE_NAME, "secure-cookie"));
        when(hmacValidator.validateHMACSHA256("secure-cookie", sessionId.getSessionId())).thenReturn(false);
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test
    public void shouldReturnSessionIdWhenCheckingNotRequiredButSessionCookieAndSecureCookieMatchUp() throws Exception {
        SessionId sessionId = SessionId.createNewSessionId();
        Map<String, Cookie> cookies = ImmutableMap.of(SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, sessionId.toString()), SECURE_COOKIE_NAME, new NewCookie(SECURE_COOKIE_NAME, "secure-cookie"));
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        when(hmacValidator.validateHMACSHA256("secure-cookie", sessionId.getSessionId())).thenReturn(true);
        when(idpSessionRepository.containsSession(sessionId)).thenReturn(true);
        new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test
    public void shouldThrowCookieNotFoundExceptionWhenCheckingRequiredButNoCookies() throws Exception {
        Map<String, Cookie> cookies = ImmutableMap.of();
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        try {
            new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
            fail("we wanted an exception but we got none");
        } catch (SessionIdCookieNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Unable to locate session from session cookie");
        }
    }

    @Test
    public void shouldThrowSecureCookieNotFoundExceptionWhenCheckingRequiredButNoSessionIdCookie() throws Exception {
        Map<String, Cookie> cookies = ImmutableMap.of();
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        try {
            new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
            fail("we wanted an exception but we got none");
        } catch (SessionIdCookieNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Unable to locate session from session cookie");
        }
    }

    @Test
    public void shouldThrowSecureCookieNotFoundExceptionWhenCheckingRequiredButNoSecureCookie() throws Exception {
        Map<String, Cookie> cookies = ImmutableMap.of(
                SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, "some-session-id")
        );
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        try {
            new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
            fail("we wanted an exception but we got none");
        } catch (SecureCookieNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Secure cookie not found.");
        }
    }

    @Test
    public void shouldThrowInvalidSecureExceptionWhenCheckingRequiredButSessionCookieIsSetToNoCurrentValue() throws Exception {
        Map<String, Cookie> cookies = ImmutableMap.of(
                SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, "session-id"),
                SECURE_COOKIE_NAME, new NewCookie(SECURE_COOKIE_NAME, NO_CURRENT_SESSION_COOKIE_VALUE)
        );
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        try {
            new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
            fail("we wanted an exception but we got none");
        } catch (InvalidSecureCookieException e) {
            assertThat(e.getMessage()).isEqualTo("Secure cookie was set to deleted session value, indicating a previously completed session.");
        }
    }

    @Test
    public void shoulThrowInvalidSecureCookieExceptionWhenCheckingRequiredButSessionCookieAndSecureCookieDontMatchUp() throws Exception {
        SessionId sessionId = SessionId.createNewSessionId();
        Map<String, Cookie> cookies = ImmutableMap.of(
                SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, sessionId.toString()),
                SECURE_COOKIE_NAME, new NewCookie(SECURE_COOKIE_NAME, "secure-cookie")
        );
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        when(hmacValidator.validateHMACSHA256("secure-cookie", sessionId.getSessionId())).thenReturn(false);
        try {
            new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
            fail("we wanted an exception but we got none");
        } catch (InvalidSecureCookieException e) {
            assertThat(e.getMessage()).isEqualTo("Secure cookie value not valid.");
        }
    }

    @Test (expected = SessionNotFoundException.class)
    public void shouldThrowNotFoundIfSessionNotActive() throws Exception {
        SessionId sessionId = SessionId.createNewSessionId();
        Map<String, Cookie> cookies = ImmutableMap.of(
                SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, sessionId.toString()),
                SECURE_COOKIE_NAME, new NewCookie(SECURE_COOKIE_NAME, "secure-cookie")
        );
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        when(hmacValidator.validateHMACSHA256("secure-cookie", sessionId.getSessionId())).thenReturn(true);
        new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }


    @Test
    public void shouldIgnoreSecureCookieIfSecureCookiesNotEnabled() throws Exception {
        SessionId sessionId = SessionId.createNewSessionId();
        Map<String, Cookie> cookies = ImmutableMap.of(
                SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, sessionId.toString()),
                SECURE_COOKIE_NAME, new NewCookie(SECURE_COOKIE_NAME, "secure-cookies")
        );
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        when(idpSessionRepository.containsSession(sessionId)).thenReturn(true);
        new SessionCookieValueMustExistAsASessionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, false).filter(containerRequestContext);
    }
}
