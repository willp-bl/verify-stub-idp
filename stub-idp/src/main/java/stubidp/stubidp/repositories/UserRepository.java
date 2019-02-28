package stubidp.stubidp.repositories;

import stubidp.stubidp.domain.DatabaseEidasUser;
import stubidp.stubidp.domain.DatabaseIdpUser;

import java.util.Collection;

public interface UserRepository {
    Collection<DatabaseIdpUser> getUsersForIdp(String idpFriendlyName);
    void addOrUpdateUserForIdp(String idpFriendlyName, DatabaseIdpUser user);
    void addOrUpdateEidasUserForStubCountry(String stubCountryName, DatabaseEidasUser eidasUser);
    void deleteUserFromIdp(String idpFriendlyName, String username);
    Collection<DatabaseEidasUser> getUsersForCountry(String friendlyName);
}
