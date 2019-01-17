package uk.gov.ida.stub.idp.views;

import java.util.Optional;

public class RegistrationPageView extends IdpPageView {

    public RegistrationPageView(String name, String idpId, String errorMessage, String assetId, String csrfToken) {
        super("registrationPage.ftl", name, idpId, errorMessage, assetId, Optional.ofNullable(csrfToken));
    }

    public String getPageTitle() {
        return String.format("Registration for %s", getName());
    }

}
