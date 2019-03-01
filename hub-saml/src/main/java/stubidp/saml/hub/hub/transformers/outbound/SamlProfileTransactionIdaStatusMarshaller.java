package stubidp.saml.hub.hub.transformers.outbound;

import com.google.common.collect.ImmutableMap;
import stubidp.saml.utils.core.OpenSamlXmlObjectFactory;
import stubidp.saml.utils.core.domain.DetailedStatusCode;
import stubidp.saml.utils.core.domain.TransactionIdaStatus;
import stubidp.saml.utils.core.transformers.outbound.IdaStatusMarshaller;

import javax.inject.Inject;

/**
 * This class is the same as TransactionIdaStatusMarshaller except that TransactionIdaStatus.NoMatchingServiceMatchFromHub
 * is mapped to DetailedStatusCode.SamlProfileNoMatchingServiceMatchFromHub. This is because the saml profile specifies that a
 * no-match (which has no Assertions) should have Status of Responder. TransactionIdaStatusMarshaller is kept in the package
 * for backwards compatibility (RPs receive Success:no-match at time of writing) but the goal is to deprecate it
 * (but this might take years).
 */
public class SamlProfileTransactionIdaStatusMarshaller extends IdaStatusMarshaller<TransactionIdaStatus> {

    private static final ImmutableMap<TransactionIdaStatus, DetailedStatusCode> REST_TO_SAML_CODES =
            ImmutableMap.<TransactionIdaStatus, DetailedStatusCode>builder()
                    .put(TransactionIdaStatus.Success, DetailedStatusCode.Success)
                    .put(TransactionIdaStatus.NoAuthenticationContext, DetailedStatusCode.NoAuthenticationContext)
                    .put(TransactionIdaStatus.NoMatchingServiceMatchFromHub, DetailedStatusCode.SamlProfileNoMatchingServiceMatchFromHub)
                    .put(TransactionIdaStatus.AuthenticationFailed, DetailedStatusCode.AuthenticationFailed)
                    .put(TransactionIdaStatus.RequesterError, DetailedStatusCode.RequesterErrorFromIdpAsSentByHub)
                    .build();

    @Inject
    public SamlProfileTransactionIdaStatusMarshaller(OpenSamlXmlObjectFactory samlObjectFactory) {
        super(samlObjectFactory);
    }


    @Override
    protected DetailedStatusCode getDetailedStatusCode(TransactionIdaStatus originalStatus) {
        return REST_TO_SAML_CODES.get(originalStatus);
    }
}
