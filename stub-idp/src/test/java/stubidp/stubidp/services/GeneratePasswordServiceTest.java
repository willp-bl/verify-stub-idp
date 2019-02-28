package stubidp.stubidp.services;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import stubidp.stubidp.services.GeneratePasswordService;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratePasswordServiceTest {

    @Test
    public void saltedPasswordsShouldVerifyWithBCrypt() throws Exception {
        GeneratePasswordService generatePasswordService = new GeneratePasswordService();

        final String password = generatePasswordService.generateCandidatePassword();
        final String hash = generatePasswordService.getHashedPassword(password);

        assertThat(BCrypt.checkpw(password, hash)).isTrue();
    }

}