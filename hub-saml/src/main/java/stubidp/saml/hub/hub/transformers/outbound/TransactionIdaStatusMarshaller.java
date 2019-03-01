package stubidp.saml.hub.hub.transformers.outbound;

import com.google.common.collect.ImmutableMap;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;
import stubidp.saml.utils.core.domain.DetailedStatusCode;
import stubidp.saml.utils.core.domain.TransactionIdaStatus;
import stubidp.saml.utils.core.transformers.outbound.IdaStatusMarshaller;

import javax.inject.Inject;

public class TransactionIdaStatusMarshaller extends IdaStatusMarshaller<TransactionIdaStatus> {

    private static final ImmutableMap<TransactionIdaStatus, DetailedStatusCode> REST_TO_SAML_CODES =
            ImmutableMap.<TransactionIdaStatus, DetailedStatusCode>builder()
                    .put(TransactionIdaStatus.Success, DetailedStatusCode.Success)
                    .put(TransactionIdaStatus.NoAuthenticationContext, DetailedStatusCode.NoAuthenticationContext)
                    .put(TransactionIdaStatus.NoMatchingServiceMatchFromHub, DetailedStatusCode.NoMatchingServiceMatchFromHub)
                    .put(TransactionIdaStatus.AuthenticationFailed, DetailedStatusCode.AuthenticationFailed)
                    .put(TransactionIdaStatus.RequesterError, DetailedStatusCode.RequesterErrorFromIdpAsSentByHub)
                    .build();

    @Inject
    public TransactionIdaStatusMarshaller(OpenSamlXmlObjectFactory samlObjectFactory) {
        super(samlObjectFactory);
    }


    @Override
    protected DetailedStatusCode getDetailedStatusCode(TransactionIdaStatus originalStatus) {
        return REST_TO_SAML_CODES.get(originalStatus);
    }
}
