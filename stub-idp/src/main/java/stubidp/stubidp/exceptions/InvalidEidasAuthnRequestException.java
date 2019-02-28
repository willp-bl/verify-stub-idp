package stubidp.stubidp.exceptions;

public class InvalidEidasAuthnRequestException extends RuntimeException {

    public InvalidEidasAuthnRequestException(String messsage) {
        super("Invalid Eidas Authn Request: " + messsage);
    }
}
