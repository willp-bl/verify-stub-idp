package stubidp.eventemitter;

import java.io.UnsupportedEncodingException;

public interface EventSender {

    void sendAuthenticated(final Event event, final String encryptedEvent) throws UnsupportedEncodingException;
}
