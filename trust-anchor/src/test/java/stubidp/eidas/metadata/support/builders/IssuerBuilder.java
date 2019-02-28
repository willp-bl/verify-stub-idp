package stubidp.eidas.metadata.support.builders;

import org.opensaml.saml.saml2.core.Issuer;
import stubidp.eidas.metadata.support.TestSamlObjectFactory;
import stubidp.test.devpki.TestCertificateStrings;

import java.util.Optional;

public class IssuerBuilder {

    private TestSamlObjectFactory testSamlObjectFactory = new TestSamlObjectFactory();
    private Optional<String> issuerId = Optional.of(TestCertificateStrings.TEST_ENTITY_ID);
    private String format = null;

    public static IssuerBuilder anIssuer() {
        return new IssuerBuilder();
    }

    public Issuer build() {

        Issuer issuer = testSamlObjectFactory.createIssuer(issuerId.orElse(null));

        issuer.setFormat(format);

        return issuer;
    }

    public IssuerBuilder withIssuerId(String issuerId) {
        this.issuerId = Optional.ofNullable(issuerId);
        return this;
    }


    public IssuerBuilder withFormat(String format) {
        this.format = format;
        return this;
    }
}
