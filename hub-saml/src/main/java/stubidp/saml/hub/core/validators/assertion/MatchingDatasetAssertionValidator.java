package stubidp.saml.hub.core.validators.assertion;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import stubidp.saml.extensions.IdaConstants;
import stubidp.saml.extensions.extensions.Address;
import stubidp.saml.extensions.extensions.Date;
import stubidp.saml.extensions.extensions.Gender;
import stubidp.saml.extensions.extensions.PersonName;
import stubidp.saml.hub.hub.exception.SamlValidationException;

import javax.xml.namespace.QName;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static stubidp.saml.extensions.validation.SamlTransformationErrorManager.warn;
import static stubidp.saml.hub.core.errors.SamlTransformationErrorFactory.attributeStatementEmpty;
import static stubidp.saml.hub.core.errors.SamlTransformationErrorFactory.attributeWithIncorrectType;
import static stubidp.saml.hub.core.errors.SamlTransformationErrorFactory.emptyAttribute;
import static stubidp.saml.hub.core.errors.SamlTransformationErrorFactory.invalidAttributeNameFormat;
import static stubidp.saml.hub.core.errors.SamlTransformationErrorFactory.mdsAttributeNotRecognised;
import static stubidp.saml.hub.core.errors.SamlTransformationErrorFactory.mdsMultipleStatements;
import static stubidp.saml.hub.core.errors.SamlTransformationErrorFactory.mdsStatementMissing;

public class MatchingDatasetAssertionValidator {

    private final DuplicateAssertionValidator duplicateAssertionValidator;

    public MatchingDatasetAssertionValidator(DuplicateAssertionValidator duplicateAssertionValidator) {
        this.duplicateAssertionValidator = duplicateAssertionValidator;
    }

    private static final Set<String> VALID_ATTRIBUTE_NAMES_1_1 = Set.of(
        IdaConstants.Attributes_1_1.Firstname.NAME,
        IdaConstants.Attributes_1_1.Middlename.NAME,
        IdaConstants.Attributes_1_1.Surname.NAME,
        IdaConstants.Attributes_1_1.Gender.NAME,
        IdaConstants.Attributes_1_1.DateOfBirth.NAME,
        IdaConstants.Attributes_1_1.CurrentAddress.NAME,
        IdaConstants.Attributes_1_1.PreviousAddress.NAME
    );

    private static final Set<String> VALID_ATTRIBUTE_NAME_FORMATS = Set.of(
        Attribute.UNSPECIFIED
    );

    private static final Map<String, QName> VALID_TYPE_FOR_ATTRIBUTE = Map.<String, QName>ofEntries(
        Map.entry(IdaConstants.Attributes_1_1.Firstname.NAME, PersonName.TYPE_NAME),
        Map.entry(IdaConstants.Attributes_1_1.Middlename.NAME, PersonName.TYPE_NAME),
        Map.entry(IdaConstants.Attributes_1_1.Surname.NAME, PersonName.TYPE_NAME),
        Map.entry(IdaConstants.Attributes_1_1.Gender.NAME, Gender.TYPE_NAME),
        Map.entry(IdaConstants.Attributes_1_1.DateOfBirth.NAME, Date.TYPE_NAME),
        Map.entry(IdaConstants.Attributes_1_1.CurrentAddress.NAME, Address.TYPE_NAME),
        Map.entry(IdaConstants.Attributes_1_1.PreviousAddress.NAME, Address.TYPE_NAME)
    );

    public void validate(Assertion assertion, String responseIssuerId) {
        duplicateAssertionValidator.validateMatchingDataSetAssertion(assertion, responseIssuerId);
        validateAttributes(assertion);
    }

    private void validateAttributes(Assertion assertion) {
        final List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
        if (attributeStatements.isEmpty()) throw new SamlValidationException(mdsStatementMissing());
        if (attributeStatements.size() > 1) throw new SamlValidationException(mdsMultipleStatements());

        final List<Attribute> attributes = attributeStatements.get(0).getAttributes();
        if (attributes.isEmpty()) throw new SamlValidationException(attributeStatementEmpty(assertion.getID()));

        attributes.forEach(this::validateAttribute);
    }

    private void validateAttribute(Attribute attribute) {
        String attributeName = attribute.getName();
        if (!VALID_ATTRIBUTE_NAMES_1_1.contains(attributeName))
            throw new SamlValidationException(mdsAttributeNotRecognised(attributeName));

        List<XMLObject> attributeValues = attribute.getAttributeValues();
        if(attributeValues.isEmpty())
            throw new SamlValidationException(emptyAttribute(attributeName));

        QName schemaType = attributeValues.get(0).getSchemaType();
        if(!VALID_TYPE_FOR_ATTRIBUTE.get(attributeName).equals(schemaType))
            throw new SamlValidationException(attributeWithIncorrectType(attributeName, VALID_TYPE_FOR_ATTRIBUTE.get(attributeName), schemaType));

        if(!VALID_ATTRIBUTE_NAME_FORMATS.contains(attribute.getNameFormat()))
            warn(invalidAttributeNameFormat(attribute.getNameFormat()));
    }
}
