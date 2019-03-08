package stubidp.saml.hub.hub.factories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Attribute;
import stubidp.saml.hub.hub.factories.AttributeQueryAttributeFactory;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;
import stubidp.saml.utils.core.test.OpenSAMLRunner;
import stubidp.saml.hub.hub.domain.UserAccountCreationAttribute;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class AttributeQueryAttributeFactoryTest {

    private AttributeQueryAttributeFactory attributeQueryAttributeFactory;

    @Before
    public void setUp() throws Exception {
        attributeQueryAttributeFactory = new AttributeQueryAttributeFactory(new OpenSamlXmlObjectFactory());
    }

    @Test
    public void createAttribute_shouldPopulateAttributeNameFromUserAccountCreationAttributeValue(){
        UserAccountCreationAttribute userAccountCreationAttribute = UserAccountCreationAttribute.CURRENT_ADDRESS;

        Attribute attribute = attributeQueryAttributeFactory.createAttribute(userAccountCreationAttribute);

        assertThat(attribute.getName()).isEqualTo("currentaddress");
    }

    @Test
    public void createAttribute_shouldPopulateAttributeNameFormatWithUnspecifiedFormat(){
        UserAccountCreationAttribute userAccountCreationAttribute = UserAccountCreationAttribute.CURRENT_ADDRESS;

        Attribute attribute = attributeQueryAttributeFactory.createAttribute(userAccountCreationAttribute);

        assertThat(attribute.getNameFormat()).isEqualTo("urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified");
    }

    @Test
    public void createAttribute_shouldNotSetFriendlyName(){
        UserAccountCreationAttribute userAccountCreationAttribute = UserAccountCreationAttribute.CURRENT_ADDRESS;

        Attribute attribute = attributeQueryAttributeFactory.createAttribute(userAccountCreationAttribute);

        assertThat(attribute.getFriendlyName()).isNull();
    }
}