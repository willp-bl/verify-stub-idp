package stubidp.stubidp.views;

public enum SamlMessageType {
    SAML_RESPONSE ("SAMLResponse") ;

    SamlMessageType(String formName) {
        this.formName = formName;
    }

    private String formName;
    public String toString() {
        return formName;
    }
}