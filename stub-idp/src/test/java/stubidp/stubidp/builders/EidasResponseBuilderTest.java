package stubidp.stubidp.builders;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import stubidp.saml.extensions.IdaSamlBootstrap;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EidasResponseBuilderTest {

    @BeforeAll
    public static void setUpClass() {
        IdaSamlBootstrap.bootstrap();
    }

    @BeforeEach
    public void setUp() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @AfterEach
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldCreateEidasResponseWithRequiredFields() throws Exception {
        List<Attribute> attributes = Collections.emptyList();
        DateTime issueInstant = DateTime.now().minusSeconds(2);
        DateTime assertionIssueInstant = DateTime.now().minusSeconds(1);
        DateTime authnStatementIssueInstant = DateTime.now();

        Response response = EidasResponseBuilder.createEidasResponse("responseIssuerId", "statusCodeValue",
                "pid", "loa", attributes, "inResponseTo", issueInstant, assertionIssueInstant, authnStatementIssueInstant, "destinationUrl", "connectorNodeIssuerId");
        Assertion assertion = response.getAssertions().get(0);
        AuthnStatement authnStatement = assertion.getAuthnStatements().get(0);

        assertThat(assertion.getAttributeStatements().get(0).getAttributes()).isEqualTo(attributes);
        assertThat(authnStatement.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef()).isEqualTo("loa");
        assertThat(authnStatement.getAuthnInstant().getMillis()).isEqualTo(authnStatementIssueInstant.getMillis());
        assertThat(assertion.getSubject().getNameID().getValue()).isEqualTo("UK/EU/pid");
        assertThat(assertion.getSubject().getNameID().getFormat()).isEqualTo(NameIDType.PERSISTENT);
        assertThat(assertion.getSubject().getSubjectConfirmations().get(0).getSubjectConfirmationData().getInResponseTo()).isEqualTo("inResponseTo");
        assertThat(assertion.getSubject().getSubjectConfirmations().get(0).getSubjectConfirmationData().getRecipient()).isEqualTo("destinationUrl");
        assertThat(assertion.getSubject().getSubjectConfirmations().get(0).getMethod()).isEqualTo(SubjectConfirmation.METHOD_BEARER);
        assertThat(assertion.getIssuer().getValue()).isEqualTo("responseIssuerId");
        assertThat(assertion.getConditions().getNotBefore().getMillis()).isEqualTo(DateTime.now().getMillis());
        assertThat(assertion.getConditions().getNotOnOrAfter().getMillis()).isEqualTo(DateTime.now().plusMinutes(5).getMillis());
        assertThat(assertion.getConditions().getAudienceRestrictions().get(0).getAudiences().get(0).getAudienceURI()).isEqualTo("connectorNodeIssuerId");
        assertThat(response.getIssuer().getValue()).isEqualTo("responseIssuerId");
        assertThat(response.getID()).isNotBlank();
        assertThat(response.getInResponseTo()).isEqualTo("inResponseTo");
        assertThat(response.getDestination()).isEqualTo("destinationUrl");
        assertThat(response.getIssueInstant().getMillis()).isEqualTo(issueInstant.getMillis());
        assertThat(response.getStatus().getStatusCode().getValue()).isEqualTo("statusCodeValue");
    }
}