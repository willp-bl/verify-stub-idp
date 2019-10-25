package stubidp.stubidp.exceptions;

public class IdpNotFoundException extends RuntimeException {
    public IdpNotFoundException(String message) {
        super(message);
    }
}
