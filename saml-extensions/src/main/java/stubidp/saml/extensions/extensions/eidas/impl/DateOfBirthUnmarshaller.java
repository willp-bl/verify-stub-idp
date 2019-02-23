package stubidp.saml.extensions.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import stubidp.saml.extensions.extensions.eidas.DateOfBirth;

public class DateOfBirthUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        DateOfBirth dateOfBirth = (DateOfBirth) samlObject;
        dateOfBirth.setDateOfBirth(DateOfBirthImpl.DATE_OF_BIRTH_FORMAT.parseLocalDate(elementContent));
    }
}
