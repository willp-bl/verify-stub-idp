package stubidp.saml.utils.hub.transformers.outbound;

import com.google.common.collect.ImmutableMap;
import stubidp.saml.utils.core.domain.DetailedStatusCode;
import stubidp.saml.utils.core.domain.UnknownUserCreationIdaStatus;
import stubidp.saml.utils.core.transformers.outbound.IdaStatusMarshaller;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;

public class UnknownUserCreationIdaStatusMarshaller extends IdaStatusMarshaller<UnknownUserCreationIdaStatus> {

    private static final ImmutableMap<UnknownUserCreationIdaStatus, DetailedStatusCode> REST_TO_SAML_CODES =
            ImmutableMap.<UnknownUserCreationIdaStatus, DetailedStatusCode>builder()
                    .put(UnknownUserCreationIdaStatus.CreateFailure, DetailedStatusCode.UnknownUserCreateFailure)
                    .put(UnknownUserCreationIdaStatus.Success, DetailedStatusCode.UnknownUserCreateSuccess)
                    .put(UnknownUserCreationIdaStatus.NoAttributeFailure, DetailedStatusCode.UnknownUserNoAttributeFailure)
                    .build();

    public UnknownUserCreationIdaStatusMarshaller(OpenSamlXmlObjectFactory samlObjectFactory) {
        super(samlObjectFactory);
    }

    @Override
    protected DetailedStatusCode getDetailedStatusCode(UnknownUserCreationIdaStatus originalStatus) {
        return REST_TO_SAML_CODES.get(originalStatus);
    }
}
