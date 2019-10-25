package stubidp.saml.extensions.extensions.eidas.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import stubidp.saml.Utils;
import stubidp.saml.extensions.extensions.eidas.PlaceOfBirth;
import stubidp.saml.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class PlaceOfBirthUnmarshallerTest extends OpenSAMLRunner {
    @Test
    public void shouldUnmarshallPlaceOfBirth() throws Exception {
        final PlaceOfBirth placeOfBirth = Utils.unmarshall("" +
                "<saml2:AttributeValue " +
                "   xmlns:eidas-natural=\"http://eidas.europa.eu/attributes/naturalperson\"\n " +
                "   xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n " +
                "   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "   xsi:type=\"eidas-natural:PlaceOfBirthType\">\n" +
                "Peterborough" +
                "</saml2:AttributeValue>"
        );

        assertThat(placeOfBirth.getPlaceOfBirth()).isEqualTo("Peterborough");
    }
}
