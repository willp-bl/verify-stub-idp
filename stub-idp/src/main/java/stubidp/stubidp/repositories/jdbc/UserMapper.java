package stubidp.stubidp.repositories.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import stubidp.stubidp.domain.DatabaseEidasUser;
import stubidp.stubidp.domain.DatabaseIdpUser;
import stubidp.stubidp.repositories.jdbc.json.EidasUserJson;
import stubidp.stubidp.repositories.jdbc.json.IdpUserJson;
import stubidp.stubidp.utils.Exceptions;

import javax.inject.Singleton;

@Singleton
public class UserMapper {

    private final ObjectMapper mapper;

    public UserMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public User mapFrom(String idpFriendlyName, DatabaseIdpUser idpUser) {
        String idpUserAsJson = Exceptions.uncheck(() -> mapper.writeValueAsString(idpUser));

        return new User(
            null,
            idpUser.getUsername(),
            idpUser.getPassword(),
            idpFriendlyName,
            idpUserAsJson
        );
    }

    public User mapFrom(String stubCountryFriendlyName, DatabaseEidasUser eidasUser) {
        String eidasUserAsJson = Exceptions.uncheck(() -> mapper.writeValueAsString(eidasUser));

        return new User(
                null,
                eidasUser.getUsername(),
                eidasUser.getPassword(),
                stubCountryFriendlyName,
                eidasUserAsJson
        );
    }

    public DatabaseIdpUser mapToIdpUser(User user) {
        IdpUserJson idpUserJson = Exceptions.uncheck(() -> mapper.readValue(user.getData(), IdpUserJson.class));

        return new DatabaseIdpUser(
            idpUserJson.getUsername(),
            idpUserJson.getPersistentId(),
            idpUserJson.getPassword(),
            idpUserJson.getFirstnames(),
            idpUserJson.getMiddleNames(),
            idpUserJson.getSurnames(),
            idpUserJson.getGender(),
            idpUserJson.getDateOfBirths(),
            idpUserJson.getAddresses(),
            idpUserJson.getLevelOfAssurance()
        );
    }

    public DatabaseEidasUser mapToEidasUser(User user) {
        EidasUserJson eidasUserJson = Exceptions.uncheck(() -> mapper.readValue(user.getData(), EidasUserJson.class));

        return new DatabaseEidasUser(
                eidasUserJson.getUsername(),
                eidasUserJson.getPersistentId(),
                eidasUserJson.getPassword(),
                eidasUserJson.getFirstname(),
                eidasUserJson.getNonLatinFirstname(),
                eidasUserJson.getSurname(),
                eidasUserJson.getNonLatinSurname(),
                eidasUserJson.getDateOfBirth(),
                eidasUserJson.getLevelOfAssurance()
        );
    }
}
