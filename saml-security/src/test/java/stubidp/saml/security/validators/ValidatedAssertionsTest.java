package stubidp.saml.security.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Assertion;
import stubidp.saml.security.saml.OpenSAMLRunner;
import stubidp.saml.security.saml.builders.AssertionBuilder;
import stubidp.saml.security.saml.builders.AttributeStatementBuilder;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class ValidatedAssertionsTest {
    @Test
    public void should_returnMatchingDatasetAssertion() {
        Assertion mdsAssertion = AssertionBuilder.anAssertion().addAttributeStatement(AttributeStatementBuilder.anAttributeStatement().build()).build();
        List<Assertion> assertions = asList(AssertionBuilder.anAssertion().build(), mdsAssertion);

        ValidatedAssertions validatedAssertions = new ValidatedAssertions(assertions);

        assertThat(validatedAssertions.getMatchingDatasetAssertion().get()).isEqualTo(mdsAssertion);
    }

    @Test
    public void should_returnAuthnStatementAssertion() {
        Assertion mdsAssertion = AssertionBuilder.anAssertion().addAttributeStatement(AttributeStatementBuilder.anAttributeStatement().build()).build();
        Assertion authnStatementAssertion = AssertionBuilder.anAssertion().build();
        List<Assertion> assertions = asList(mdsAssertion, authnStatementAssertion);

        ValidatedAssertions validatedAssertions = new ValidatedAssertions(assertions);

        assertThat(validatedAssertions.getAuthnStatementAssertion().get()).isEqualTo(authnStatementAssertion);
    }

    @Test
    public void should_supportAnEmptyListOfAssertions() {
        ValidatedAssertions validatedAssertions = new ValidatedAssertions(emptyList());

        assertThat(validatedAssertions.getAuthnStatementAssertion().isPresent()).isFalse();
        assertThat(validatedAssertions.getMatchingDatasetAssertion().isPresent()).isFalse();
    }
}
