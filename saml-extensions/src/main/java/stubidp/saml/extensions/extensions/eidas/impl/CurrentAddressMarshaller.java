package stubidp.saml.extensions.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.ElementSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.w3c.dom.Element;
import stubidp.saml.extensions.extensions.eidas.CurrentAddress;

public class CurrentAddressMarshaller extends AbstractSAMLObjectMarshaller {

    public static final Marshaller MARSHALLER = new CurrentAddressMarshaller();

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject samlObject, Element domElement) throws MarshallingException {
        CurrentAddress currentAddress = (CurrentAddress) samlObject;
        ElementSupport.appendTextContent(domElement, currentAddress.getCurrentAddress());
    }
}
