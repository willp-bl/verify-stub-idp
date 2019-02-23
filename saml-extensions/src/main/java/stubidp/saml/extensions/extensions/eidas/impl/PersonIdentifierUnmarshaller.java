package stubidp.saml.extensions.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import stubidp.saml.extensions.extensions.eidas.PersonIdentifier;

public class PersonIdentifierUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        PersonIdentifier personIdentifier = (PersonIdentifier) samlObject;
        personIdentifier.setPersonIdentifier(elementContent);
    }
}
