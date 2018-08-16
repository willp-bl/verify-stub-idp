package uk.gov.ida.stub.idp.dropwizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.cli.Command;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.assertj.core.util.Lists;
import uk.gov.ida.dropwizard.logstash.LogstashBundle;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;

import javax.validation.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ListRequiredEnvVarsCommand extends Command {

    public ListRequiredEnvVarsCommand() {
        super("list-required-env-vars", "display a list of the required environment variables to use this app");
    }

    @Override
    public void configure(Subparser subparser) {

    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) {
        bootstrap.addBundle(new LogstashBundle());

        // this needs to be false for the ListRequiredEnvVarsCommand
        final EnvironmentVariableSubstitutor substitutor = new EnvironmentVariableSubstitutor(false);

        try {
            parseConfiguration((ConfigurationFactoryFactory<StubIdpConfiguration>)bootstrap.getConfigurationFactoryFactory(),
                    new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), substitutor),
                    bootstrap.getValidatorFactory().getValidator(),
                    "configuration/stub-idp.yml",
                    StubIdpConfiguration.class,
                    bootstrap.getObjectMapper());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        final ArrayList<String> sortedVariableList = Lists.newArrayList(substitutor.getRequiredEnvironmentVariables());
        Collections.sort(sortedVariableList);
        for(String variable:sortedVariableList) {
            System.out.println(variable);
        }
    }

    /**
     * copied from https://github.com/dropwizard/dropwizard/blob/master/dropwizard-core/src/main/java/io/dropwizard/cli/ConfiguredCommand.java
     * Apache Licence 2.0
     */
    private StubIdpConfiguration parseConfiguration(ConfigurationFactoryFactory<StubIdpConfiguration> configurationFactoryFactory,
                                                    ConfigurationSourceProvider provider,
                                                    Validator validator,
                                                    String path,
                                                    Class<StubIdpConfiguration> klass,
                                                    ObjectMapper objectMapper) throws IOException, ConfigurationException {
        final ConfigurationFactory<StubIdpConfiguration> configurationFactory = configurationFactoryFactory
                .create(klass, validator, objectMapper, "dw");
        if (path != null) {
            try {
                return configurationFactory.build(provider, path);
            } catch (Exception e) {
                // do nothing - i.e. don't spam stderr because env vars are not set
                return null;
            }
        }
        return configurationFactory.build();
    }
}
