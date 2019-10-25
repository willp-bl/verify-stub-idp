package stubidp.saml.stubidp.test.builders;

import java.util.Optional;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;

public class SubjectConfirmationBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<String> method = Optional.ofNullable(SubjectConfirmation.METHOD_BEARER);
    private Optional<SubjectConfirmationData> subjectConfirmationData = Optional.ofNullable(SubjectConfirmationDataBuilder.aSubjectConfirmationData().build());

    public static SubjectConfirmationBuilder aSubjectConfirmation() {
        return new SubjectConfirmationBuilder();
    }

    public SubjectConfirmation build() {
        SubjectConfirmation subjectConfirmation = openSamlXmlObjectFactory.createSubjectConfirmation();

        if (method.isPresent()) {
            subjectConfirmation.setMethod(method.get());
        }

        if (subjectConfirmationData.isPresent()) {
            subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData.get());
        }

        return subjectConfirmation;
    }

    public SubjectConfirmationBuilder withMethod(String method) {
        this.method = Optional.ofNullable(method);
        return this;
    }

    public SubjectConfirmationBuilder withSubjectConfirmationData(SubjectConfirmationData subjectConfirmationData) {
        this.subjectConfirmationData = Optional.ofNullable(subjectConfirmationData);
        return this;
    }
}
