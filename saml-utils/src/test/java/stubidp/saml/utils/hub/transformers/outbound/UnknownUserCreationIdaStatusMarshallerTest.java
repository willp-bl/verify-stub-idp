package stubidp.saml.utils.hub.transformers.outbound;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import stubidp.saml.extensions.domain.SamlStatusCode;
import stubidp.saml.utils.OpenSAMLRunner;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;
import stubidp.saml.utils.core.domain.UnknownUserCreationIdaStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class UnknownUserCreationIdaStatusMarshallerTest extends OpenSAMLRunner {

    private UnknownUserCreationIdaStatusMarshaller unknownUserCreationIdaStatusToSamlStatusMarshaller;

    @BeforeEach
    public void setUp() throws Exception {
        unknownUserCreationIdaStatusToSamlStatusMarshaller = new UnknownUserCreationIdaStatusMarshaller(new OpenSamlXmlObjectFactory());
    }

    @Test
    public void transform_shouldTransformUnknownUserCreationSuccess() throws Exception {

        Status transformedStatus = unknownUserCreationIdaStatusToSamlStatusMarshaller.toSamlStatus(UnknownUserCreationIdaStatus.Success);

        assertThat(transformedStatus.getStatusCode().getValue()).isEqualTo(StatusCode.SUCCESS);
        assertThat(transformedStatus.getStatusCode().getStatusCode().getValue()).isEqualTo(SamlStatusCode.CREATED);
    }

    @Test
    public void transform_shouldTransformUnknownUserCreationFailure() throws Exception {

        Status transformedStatus = unknownUserCreationIdaStatusToSamlStatusMarshaller.toSamlStatus(UnknownUserCreationIdaStatus.CreateFailure);

        assertThat(transformedStatus.getStatusCode().getValue()).isEqualTo(StatusCode.RESPONDER);
        assertThat(transformedStatus.getStatusCode().getStatusCode()).isNotNull();
        assertThat(transformedStatus.getStatusCode().getStatusCode().getValue()).isEqualTo(SamlStatusCode.CREATE_FAILURE);
    }
}
