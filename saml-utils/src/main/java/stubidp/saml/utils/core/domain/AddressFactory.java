package stubidp.saml.utils.core.domain;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Attribute;
import stubidp.saml.extensions.extensions.Line;

import java.util.ArrayList;
import java.util.List;

public class AddressFactory {
    public Address create(List<String> lines, String postCode, String internationalPostCode, String uprn, DateTime from, DateTime to, boolean isVerified) {
        return new Address(lines, postCode, internationalPostCode, uprn, from, to, isVerified);
    }

    public List<Address> create(Attribute attribute) {
        List<Address> addresses = new ArrayList<>();
        for (XMLObject xmlObject : attribute.getAttributeValues()) {
            stubidp.saml.extensions.extensions.Address address = (stubidp.saml.extensions.extensions.Address) xmlObject;
            addresses.add(create(address));
        }
        return addresses;
    }

    public Address create(stubidp.saml.extensions.extensions.Address addressAttributeValue) {
        List<String> lines = new ArrayList<>();
        for (Line originalLine : addressAttributeValue.getLines()) {
            lines.add(originalLine.getValue());
        }

        DateTime toDate = addressAttributeValue.getTo();

        DateTime fromDate = addressAttributeValue.getFrom();

        String postCodeString = null;
        if (addressAttributeValue.getPostCode() != null) {
            postCodeString = addressAttributeValue.getPostCode().getValue();
        }

        String internationalPostCodeString = null;
        if (addressAttributeValue.getInternationalPostCode() != null) {
            internationalPostCodeString = addressAttributeValue.getInternationalPostCode().getValue();
        }

        String uprnString = null;
        if (addressAttributeValue.getUPRN() != null) {
            uprnString = addressAttributeValue.getUPRN().getValue();
        }

        return new Address(lines, postCodeString, internationalPostCodeString, uprnString, fromDate, toDate, addressAttributeValue.getVerified());
    }
}
