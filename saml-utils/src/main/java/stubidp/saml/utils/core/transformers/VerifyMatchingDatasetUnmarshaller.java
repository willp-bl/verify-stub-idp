package stubidp.saml.utils.core.transformers;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stubidp.saml.domain.assertions.Address;
import stubidp.saml.domain.assertions.Gender;
import stubidp.saml.domain.assertions.SimpleMdsValue;
import stubidp.saml.domain.assertions.TransliterableMdsValue;
import stubidp.saml.extensions.IdaConstants;
import stubidp.saml.utils.core.domain.AddressFactory;
import stubidp.saml.extensions.extensions.PersonName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

public class VerifyMatchingDatasetUnmarshaller extends MatchingDatasetUnmarshaller {

    private static final Logger LOG = LoggerFactory.getLogger(VerifyMatchingDatasetUnmarshaller.class);
    private final AddressFactory addressFactory;

    public VerifyMatchingDatasetUnmarshaller(AddressFactory addressFactory) {
        this.addressFactory = addressFactory;
    }

    protected void transformAttribute(Attribute attribute, MatchingDatasetBuilder datasetBuilder) {
        switch (attribute.getName()) {
            case IdaConstants.Attributes_1_1.Firstname.NAME:
                datasetBuilder.addFirstNames(transformPersonNameAttribute(attribute).stream().map(TransliterableMdsValue::new).collect(Collectors.toList()));
                break;

            case IdaConstants.Attributes_1_1.Middlename.NAME:
                datasetBuilder.middlenames(transformPersonNameAttribute(attribute));
                break;

            case IdaConstants.Attributes_1_1.Surname.NAME:
                datasetBuilder.addSurnames(transformPersonNameAttribute(attribute).stream().map(TransliterableMdsValue::new).collect(Collectors.toList()));
                break;

            case IdaConstants.Attributes_1_1.Gender.NAME:
                stubidp.saml.extensions.extensions.Gender gender = (stubidp.saml.extensions.extensions.Gender) attribute.getAttributeValues().get(0);
                datasetBuilder.gender(new SimpleMdsValue<>(Gender.fromString(gender.getValue()), gender.getFrom(), gender.getTo(), gender.getVerified()));
                break;

            case IdaConstants.Attributes_1_1.DateOfBirth.NAME:
                datasetBuilder.dateOfBirth(getBirthdates(attribute));
                break;

            case IdaConstants.Attributes_1_1.CurrentAddress.NAME:
                List<Address> transformedCurrentAddresses = addressFactory.create(attribute);
                datasetBuilder.addCurrentAddresses(transformedCurrentAddresses);
                break;

            case IdaConstants.Attributes_1_1.PreviousAddress.NAME:
                List<Address> transformedPreviousAddresses = addressFactory.create(attribute);
                datasetBuilder.addPreviousAddresses(transformedPreviousAddresses);
                break;

            default:
                String errorMessage = format("Attribute {0} is not a supported Matching Dataset attribute.", attribute.getName());
                LOG.warn(errorMessage);
                throw new IllegalArgumentException(errorMessage);
        }
    }

    @Override
    protected String getPersonalIdentifier(Assertion assertion) {
        return assertion.getSubject().getNameID().getValue();
    }

    private List<SimpleMdsValue<String>> transformPersonNameAttribute(Attribute attribute) {
        List<SimpleMdsValue<String>> personNames = new ArrayList<>();

        for (XMLObject xmlObject : attribute.getAttributeValues()) {
            PersonName personName = (PersonName) xmlObject;
            personNames.add(new SimpleMdsValue<>(personName.getValue(), personName.getFrom(), personName.getTo(), personName.getVerified()));
        }

        return personNames;
    }
}
