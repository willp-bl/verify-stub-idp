package stubidp.saml.extensions.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import stubidp.saml.extensions.extensions.eidas.EidasGender;

import javax.annotation.Nullable;
import java.util.List;

public class EidasGenderImpl extends AbstractSAMLObject implements EidasGender {

    /** String to hold the gender value. */
    private String value;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected EidasGenderImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public void setValue(String s) {

        value = prepareForAssignment(value, s);
    }

    @Nullable
    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}
