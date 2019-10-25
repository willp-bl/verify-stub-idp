package stubidp.stubidp.views;

import stubidp.stubidp.Urls;

import java.util.Optional;

public class LoginPageView extends IdpPageView {

    public LoginPageView(String name, String idpId, String errorMessage, String assetId, String csrfToken) {
        super("loginPage.ftl", name, idpId, errorMessage, assetId, Optional.ofNullable(csrfToken));
    }

    public String getPageTitle() {
        return String.format("Welcome to %s", getName());
    }

    public String getAuthnPendingResource() {
        return Urls.IDP_AUTHN_PENDING_RESOURCE;
    }

    public String getNoAuthnContextResource() {
        return Urls.IDP_NO_AUTHN_CONTEXT_RESOURCE;
    }

    public String getAuthnFailureResource() {
        return Urls.IDP_AUTHN_FAILURE_RESOURCE;
    }

    public String getUpliftFailedResource() {
        return Urls.IDP_UPLIFT_FAILED_RESOURCE;
    }

    public String getFraudFailureResource() {
        return Urls.IDP_FRAUD_FAILURE_RESOURCE;
    }

    public String getRequesterErrorResource() {
        return Urls.IDP_REQUESTER_ERROR_RESOURCE;
    }

}
