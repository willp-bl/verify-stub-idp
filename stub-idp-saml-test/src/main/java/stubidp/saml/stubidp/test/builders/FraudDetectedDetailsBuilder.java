package stubidp.saml.stubidp.test.builders;

import stubidp.saml.utils.core.domain.FraudDetectedDetails;

public class FraudDetectedDetailsBuilder {

    private String eventId = "default-event-id";
    private String fraudIndicator = "IT01";

    public static FraudDetectedDetailsBuilder aFraudDetectedDetails() {
        return new FraudDetectedDetailsBuilder();
    }

    public FraudDetectedDetails build() {
        return new FraudDetectedDetails(eventId, fraudIndicator);
    }


    public FraudDetectedDetailsBuilder withFraudIndicator(String fraudIndicator) {
        this.fraudIndicator = fraudIndicator;
        return this;
    }

}
