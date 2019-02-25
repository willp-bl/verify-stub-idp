package stubidp.saml.utils.core.test;

import org.junit.runners.model.InitializationError;
import org.mockito.junit.MockitoJUnitRunner;
import stubidp.saml.extensions.IdaSamlBootstrap;

import java.lang.reflect.InvocationTargetException;

public class OpenSAMLMockitoRunner extends MockitoJUnitRunner {

    public OpenSAMLMockitoRunner(Class<?> klass) throws InitializationError, InvocationTargetException {
        super(klass);
        try {
            IdaSamlBootstrap.bootstrap();
         } catch (IdaSamlBootstrap.BootstrapException e) {
            throw new InitializationError(e);
        }
    }
}
