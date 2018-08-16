/**
 * Initially taken from https://www.dropwizard.io/1.0.0/docs/manual/testing.html#testing-commands
 */

package uk.gov.ida.stub.idp.dropwizard;

import io.dropwizard.cli.Cli;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.util.JarLocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.stub.idp.StubIdpApplication;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListRequiredEnvVarsCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;

    private final ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
    private final ByteArrayOutputStream stdErr = new ByteArrayOutputStream();
    private Cli cli;

    @Before
    public void setUp() {
        // Setup necessary mock
        final JarLocation location = mock(JarLocation.class);
        when(location.getVersion()).thenReturn(Optional.of("1.0.0"));

        // Add commands you want to test
        final Bootstrap<StubIdpConfiguration> bootstrap = new Bootstrap<>(new StubIdpApplication());
        bootstrap.addCommand(new ListRequiredEnvVarsCommand());

        // Redirect stdout and stderr to our byte streams
        System.setOut(new PrintStream(stdOut));
        System.setErr(new PrintStream(stdErr));

        // Build what'll run the command and interpret arguments
        cli = new Cli(location, bootstrap, stdOut, stdErr);
    }

    @After
    public void teardown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
    }

    @Test
    public void shouldListAllEnvVarsInTheConfigFile() throws Exception {
        final boolean success = cli.run("list-required-env-vars");

        assertThat(success).as("Exit success").isTrue();

        assertThat(stdOut.toString()).as("stdout").isEqualTo("CERT_TYPE\n" +
                "DB_URI\n" +
                "ENTITY_ID\n" +
                "EUROPEAN_IDENTITY_ENABLED\n" +
                "GRAPHITE_PREFIX\n" +
                "GRAPHITE_REPORTING_FREQUENCY\n" +
                "HUB_CONNECTOR_ENTITY_ID\n" +
                "KEY_TYPE\n" +
                "LOG_LEVEL\n" +
                "LOG_PATH\n" +
                "METADATA_ENTITY_ID\n" +
                "METADATA_TRUSTSTORE\n" +
                "METADATA_URL\n" +
                "PORT\n" +
                "STUB_COUNTRY_SIGNING_CERT\n" +
                "STUB_COUNTRY_SIGNING_PRIVATE_KEY\n" +
                "STUB_IDPS_FILE_PATH\n" +
                "STUB_IDP_BASIC_AUTH\n" +
                "STUB_IDP_HOSTNAME\n" +
                "STUB_IDP_SIGNING_CERT\n" +
                "STUB_IDP_SIGNING_PRIVATE_KEY\n" +
                "TRUSTSTORE_PASSWORD\n" +
                "TRUSTSTORE_TYPE\n");
        assertThat(stdErr.toString()).as("stderr").isEmpty();
    }
}
