package stubidp.saml.hub.hub.transformers.inbound;

import stubidp.saml.hub.hub.domain.CountryAuthenticationStatus;

public class CountryAuthenticationStatusUnmarshaller extends AuthenticationStatusUnmarshallerBase<CountryAuthenticationStatus.Status, CountryAuthenticationStatus> {
    public CountryAuthenticationStatusUnmarshaller() {
        super(new SamlStatusToCountryAuthenticationStatusCodeMapper(), new CountryAuthenticationStatus.CountryAuthenticationStatusFactory());
    }
}
