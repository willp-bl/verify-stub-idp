/**
 * This class is based on EnvironmentVariableSubstitutor from dropwizard-configuration
 * Apache Licence 2.0
 */

package uk.gov.ida.stub.idp.dropwizard;

import io.dropwizard.configuration.UndefinedEnvironmentVariableException;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Collection;

/**
 * A custom {@link StrSubstitutor} using environment variables as lookup source.
 */
public class EnvironmentVariableSubstitutor extends StrSubstitutor {

    private static final EnvironmentVariableLookup environmentVariableLookup = new EnvironmentVariableLookup();

    public EnvironmentVariableSubstitutor() {
        this(true, false);
    }

    public EnvironmentVariableSubstitutor(boolean strict) {
        this(strict, false);
    }

    /**
     * @param strict                  {@code true} if looking up undefined environment variables should throw a
     *                                {@link UndefinedEnvironmentVariableException}, {@code false} otherwise.
     * @param substitutionInVariables a flag whether substitution is done in variable names.
     * @see io.dropwizard.configuration.EnvironmentVariableLookup#EnvironmentVariableLookup(boolean)
     * @see org.apache.commons.lang3.text.StrSubstitutor#setEnableSubstitutionInVariables(boolean)
     */
    public EnvironmentVariableSubstitutor(boolean strict, boolean substitutionInVariables) {
        super(environmentVariableLookup);
        environmentVariableLookup.setStrict(strict);
        this.setEnableSubstitutionInVariables(substitutionInVariables);
    }

    public Collection<String> getRequiredEnvironmentVariables() {
        return environmentVariableLookup.getRequiredEnvironmentVariables();
    }
}
