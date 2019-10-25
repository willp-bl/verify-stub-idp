package stubidp.saml.stubidp.test.builders;

import stubidp.saml.utils.core.domain.PersistentId;

public class PersistentIdBuilder {

    private String nameId = "default-name-id";

    public static PersistentIdBuilder aPersistentId() {
        return new PersistentIdBuilder();
    }

    public PersistentId build() {
        return new PersistentId(nameId);
    }

    public PersistentIdBuilder withNameId(String persistentId) {
        this.nameId = persistentId;
        return this;
    }
}
