package uk.gov.ida.stub.idp.views;

import java.util.Optional;

public class RegistrationPageView extends IdpPageView {
    private final boolean isPreRegistration;

    public RegistrationPageView(String name, String idpId, String errorMessage, String assetId, boolean isPreRegistration, String csrfToken) {
        super("registrationPage.ftl", name, idpId, errorMessage, assetId, Optional.ofNullable(csrfToken));
        this.isPreRegistration = isPreRegistration;
    }

    public String getPageTitle() {
        return String.format("Registration for %s", getName());
    }

    public boolean isPreRegistration() {
        return isPreRegistration;
    }

}
