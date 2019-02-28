package stubidp.saml.metadata;

import org.junit.Test;
import stubidp.saml.metadata.NamelessPKIXValidationInformationResolver;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class NamelessPKIXValidationInformationResolverTest {

    @Test
    public void shouldNotSupportTrustedNameResolution() {
        NamelessPKIXValidationInformationResolver namelessPKIXValidationInformationResolver = new NamelessPKIXValidationInformationResolver(Collections.emptyList());

        assertThat(namelessPKIXValidationInformationResolver.supportsTrustedNameResolution()).isFalse();
    }
}