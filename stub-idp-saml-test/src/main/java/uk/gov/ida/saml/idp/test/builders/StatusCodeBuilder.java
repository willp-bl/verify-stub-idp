package uk.gov.ida.saml.idp.test.builders;

import java.util.Optional;
import org.opensaml.saml.saml2.core.StatusCode;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.SamlStatusCode;

public class StatusCodeBuilder {

    private static OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<String> value = Optional.ofNullable(StatusCode.SUCCESS);
    private Optional<StatusCode> subStatus = Optional.empty();

    public static StatusCodeBuilder aStatusCode() {
        return new StatusCodeBuilder();
    }

    public StatusCode build() {
        StatusCode statusCode = openSamlXmlObjectFactory.createStatusCode();

        if (value.isPresent()) {
            statusCode.setValue(value.get());
        }

        if (subStatus.isPresent()) {
            statusCode.setStatusCode(subStatus.get() );
        }

        return statusCode;
    }

    public StatusCodeBuilder withValue(String value) {
        this.value = Optional.ofNullable(value);
        return this;
    }

    public StatusCodeBuilder withSubStatusCode(StatusCode subStatusCode){
        this.subStatus = Optional.ofNullable(subStatusCode);
        return this;
    }

    public StatusCodeBuilder forMatchingService() {
        this.withSubStatusCode(aStatusCode().withValue(SamlStatusCode.MATCH).build());
        return this;
    }
}
