package uk.gov.ida.saml.idp.builders;

import static stubidp.saml.utils.core.domain.IdentityProviderAuthnStatement.createIdentityProviderAuthnStatement;
import static stubidp.saml.utils.core.domain.IdentityProviderAuthnStatement.createIdentityProviderFraudAuthnStatement;

import java.util.Optional;

import stubidp.saml.utils.core.domain.AuthnContext;
import stubidp.saml.utils.core.domain.FraudAuthnDetails;
import stubidp.saml.utils.core.domain.IdentityProviderAuthnStatement;
import stubidp.saml.utils.core.domain.IpAddress;

public class IdentityProviderAuthnStatementBuilder {

    private Optional<FraudAuthnDetails> fraudAuthnDetails = Optional.empty();
    private AuthnContext authnContext = AuthnContext.LEVEL_1;
    private Optional<IpAddress> userIpAddress = Optional.of(new IpAddress("9.9.8.8"));

    public static IdentityProviderAuthnStatementBuilder anIdentityProviderAuthnStatement() {
        return new IdentityProviderAuthnStatementBuilder();
    }

    public IdentityProviderAuthnStatement build() {
        if (fraudAuthnDetails.isPresent()) {
            return createIdentityProviderFraudAuthnStatement(fraudAuthnDetails.get(), userIpAddress.orElse(null));
        }
        return createIdentityProviderAuthnStatement(authnContext, userIpAddress.orElse(null));
    }

    public IdentityProviderAuthnStatementBuilder withAuthnContext(AuthnContext authnContext) {
        this.authnContext = authnContext;
        return this;
    }

    public IdentityProviderAuthnStatementBuilder withFraudDetails(FraudAuthnDetails fraudDetails) {
        this.fraudAuthnDetails = Optional.ofNullable(fraudDetails);
        return this;
    }

    public IdentityProviderAuthnStatementBuilder withUserIpAddress(IpAddress userIpAddress) {
        this.userIpAddress = Optional.ofNullable(userIpAddress);
        return this;
    }
}
