package stubidp.saml.extensions.extensions.impl;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import stubidp.saml.extensions.IdaConstants;
import stubidp.saml.extensions.extensions.SPType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SPTypeBuilder extends AbstractSAMLObjectBuilder<SPType> {

    @Nonnull
    @Override
    public SPType buildObject() {
        return buildObject(IdaConstants.EIDAS_NS, SPType.DEFAULT_ELEMENT_LOCAL_NAME, IdaConstants.EIDAS_PREFIX);
    }

    @Nonnull
    @Override
    public SPType buildObject(@Nullable String namespaceURI, @Nonnull @NotEmpty String localName, @Nullable String namespacePrefix) {
        return new SPTypeImpl(namespaceURI, localName, namespacePrefix);
    }
}
