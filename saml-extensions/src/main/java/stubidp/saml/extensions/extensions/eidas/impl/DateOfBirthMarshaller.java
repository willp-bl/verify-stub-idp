package stubidp.saml.extensions.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.ElementSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.w3c.dom.Element;
import stubidp.saml.extensions.extensions.eidas.DateOfBirth;

import static stubidp.saml.extensions.extensions.eidas.impl.DateOfBirthImpl.DATE_OF_BIRTH_FORMAT;

public class DateOfBirthMarshaller extends AbstractSAMLObjectMarshaller {

    public static final Marshaller MARSHALLER = new DateOfBirthMarshaller();

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject samlObject, Element domElement) throws MarshallingException {
        DateOfBirth dateOfBirth = (DateOfBirth) samlObject;
        ElementSupport.appendTextContent(domElement, DATE_OF_BIRTH_FORMAT.print(dateOfBirth.getDateOfBirth()));
    }
}
