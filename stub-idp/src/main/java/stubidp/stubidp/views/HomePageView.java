package stubidp.stubidp.views;

import stubidp.stubidp.Urls;
import stubidp.stubidp.domain.DatabaseIdpUser;
import stubidp.stubidp.views.helpers.IdpUserHelper;

import java.util.Arrays;
import java.util.Optional;

public class HomePageView extends IdpPageView {
    Optional<DatabaseIdpUser> loggedInUser;
    IdpUserHelper idpUserHelper;

    public HomePageView(String name, String idpId, String errorMessage, String assetId, Optional<DatabaseIdpUser> loggedInUser) {
        super("homePage.ftl", name, idpId, errorMessage, assetId, Optional.empty());
        this.loggedInUser = loggedInUser;
        idpUserHelper = new IdpUserHelper(loggedInUser.orElse(null));
    }

    public String getPageTitle() {
        return String.format("Welcome to %s", getName());
    }

    public boolean isUserLoggedIn() { return loggedInUser.isPresent(); }

    public String getUserFullName() {
        return String.format("%s %s", idpUserHelper.getFirstName(), idpUserHelper.getSurname());
    }

    public String getAnOrA() {
        String[] vowels = {"a","e","i","o","u"};
        return Arrays.stream(vowels).anyMatch(vowel -> super.getName().toLowerCase().startsWith(vowel)) ? "an" : "a";
    }

    public String getLogoutResource() {
        return Urls.SINGLE_IDP_LOGOUT_RESOURCE;
    }

    public String getPreRegisterResource() {
        return Urls.SINGLE_IDP_PRE_REGISTER_RESOURCE;
    }

    public String getStartPromptResource() {
        return Urls.SINGLE_IDP_START_PROMPT_RESOURCE;
    }
}
