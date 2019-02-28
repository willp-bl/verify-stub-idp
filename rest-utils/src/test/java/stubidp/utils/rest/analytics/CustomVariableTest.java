package stubidp.utils.rest.analytics;

import org.junit.Test;
import stubidp.utils.rest.analytics.CustomVariable;

import static org.junit.Assert.*;

public class CustomVariableTest {

    @Test
    public void testGetAsJson_shouldFormatJsonToPiwikCvarFormat() throws Exception {
        CustomVariable customVariable = new CustomVariable(1, "foo", "bar");

        String customVariableAsJson = customVariable.getJson();

        assertEquals(customVariableAsJson, "{\"1\":[\"foo\",\"bar\"]}");
    }
}
