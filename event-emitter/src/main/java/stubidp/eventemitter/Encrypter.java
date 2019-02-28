package stubidp.eventemitter;

public interface Encrypter {

    String encrypt(final Event event) throws EventEncryptionException;
}
