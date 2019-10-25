package stubidp.saml.stubidp.stub.domain;

import java.util.Optional;
import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.Attribute;
import stubidp.saml.utils.core.domain.PersistentId;
import stubidp.saml.utils.core.domain.AuthnContext;
import stubidp.saml.utils.core.domain.IdaSamlResponse;
import stubidp.saml.utils.core.domain.TransactionIdaStatus;

import java.net.URI;
import java.util.List;

public class InboundResponseFromHub extends IdaSamlResponse {
    private final Optional<List<Attribute>> attributes;
    private final TransactionIdaStatus status;
    private final Optional<PersistentId> persistentId;
    private final Optional<AuthnContext> authnContext;

    public InboundResponseFromHub(String responseId,
                                  DateTime issueInstant,
                                  String inResponseTo,
                                  String issuer,
                                  URI destination,
                                  Optional<List<Attribute>> attributes,
                                  TransactionIdaStatus status,
                                  Optional<PersistentId> persistentId,
                                  Optional<AuthnContext> authnContext) {
        super(responseId, issueInstant, inResponseTo, issuer, destination);
        this.attributes = attributes;
        this.status = status;
        this.persistentId = persistentId;
        this.authnContext = authnContext;
    }

    public TransactionIdaStatus getStatus() {
        return status;
    }

    public Optional<PersistentId> getPersistentId() {
        return persistentId;
    }

    public Optional<List<Attribute>> getAttributes() {
        return attributes;
    }

    public Optional<AuthnContext> getAuthnContext() {
        return authnContext;
    }
}
