package stubidp.saml.extensions.extensions.eidas.impl;

import org.junit.jupiter.api.Test;
import stubidp.saml.Utils;
import stubidp.saml.extensions.extensions.eidas.DateOfBirth;
import stubidp.saml.extensions.extensions.impl.BaseMdsSamlObjectMarshaller;
import stubidp.saml.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DateOfBirthUnmarshallerTest extends OpenSAMLRunner {
    @Test
    void shouldUnmarshallDateOfBirth() throws Exception {
        final DateOfBirth dateOfBirth = Utils.unmarshall("" +
                "<saml2:AttributeValue " +
                "   xmlns:eidas-natural=\"http://eidas.europa.eu/attributes/naturalperson\"\n " +
                "   xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n " +
                "   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "   xsi:type=\"eidas-natural:DateOfBirthType\">\n" +
                "1965-01-01" +
                "</saml2:AttributeValue>"
        );

        assertThat(BaseMdsSamlObjectMarshaller.DateFromInstant.of(dateOfBirth.getDateOfBirth())).isEqualTo("1965-01-01");
    }
}
