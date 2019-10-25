package stubidp.stubidp.exceptions;

public class IdpUserNotFoundException extends RuntimeException {
    public IdpUserNotFoundException(String message) {
        super(message);
    }
}
