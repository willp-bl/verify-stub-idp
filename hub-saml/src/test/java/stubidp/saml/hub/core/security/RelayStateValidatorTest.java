package stubidp.saml.hub.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stubidp.saml.hub.core.errors.SamlTransformationErrorFactory;
import stubidp.saml.hub.core.OpenSAMLRunner;
import stubidp.saml.utils.core.test.SamlTransformationErrorManagerTestHelper;

import static java.util.Arrays.asList;

public class RelayStateValidatorTest extends OpenSAMLRunner {

    private RelayStateValidator relayStateValidator;

    @BeforeEach
    public void setUp() throws Exception {
        relayStateValidator = new RelayStateValidator();
    }

    @Test
    public void validate_shouldCheckRelayStateLengthIsLessThanEightyOneCharactersOrRaiseException() {
        final String aStringMoreThanEightyCharacters = generateLongString();

        SamlTransformationErrorManagerTestHelper.validateFail(
                new SamlTransformationErrorManagerTestHelper.Action() {
                    @Override
                    public void execute() {
                        relayStateValidator.validate(aStringMoreThanEightyCharacters);
                    }
                },
                SamlTransformationErrorFactory.invalidRelayState(aStringMoreThanEightyCharacters)
        );

    }

    @Test
    public void validate_shouldCheckRelayStateForValidStringAndNotThrowAnException() {
        String aStringLessThanEightyCharacters = generateShortString();

        relayStateValidator.validate(aStringLessThanEightyCharacters);
    }

    @Test
    public void validate_shouldCheckForInvalidCharacters() {
        final String aString = "aStringWith";

        for (final String i : asList(">", "<", "'", "\"", "%", "&", ";")) {

            SamlTransformationErrorManagerTestHelper.validateFail(
                    new SamlTransformationErrorManagerTestHelper.Action() {
                        @Override
                        public void execute() {
                            relayStateValidator.validate(aString + i);
                        }
                    },
                    SamlTransformationErrorFactory.relayStateContainsInvalidCharacter(i, aString + i)
            );
        }
    }

    private String generateShortString() {
        return "short string";
    }


    private String generateLongString() {
        String longString = "";
        for (int i = 0; i < 82; i++) {
            longString = longString + "a";
        }
        return longString;
    }
}
