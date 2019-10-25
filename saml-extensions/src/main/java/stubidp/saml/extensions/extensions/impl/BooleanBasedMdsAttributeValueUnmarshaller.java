package stubidp.saml.extensions.extensions.impl;

import org.opensaml.core.xml.XMLObject;
import stubidp.saml.extensions.extensions.BooleanBasedMdsAttributeValue;

public class BooleanBasedMdsAttributeValueUnmarshaller extends BaseMdsSamlObjectUnmarshaller {

    @Override
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        BooleanBasedMdsAttributeValue booleanBasedMdsAttributeValue = (BooleanBasedMdsAttributeValue) samlObject;
        booleanBasedMdsAttributeValue.setValue(Boolean.valueOf(elementContent));
    }
}
