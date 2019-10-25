package stubidp.saml.extensions.extensions.eidas.impl;

import org.opensaml.saml.common.AbstractSAMLObject;
import stubidp.saml.extensions.extensions.eidas.TransliterableString;

public abstract class AbstractTransliterableString extends AbstractSAMLObject implements TransliterableString {

    private Boolean isLatinScript = true;

    protected AbstractTransliterableString(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    @Override
    public Boolean isLatinScript() {
        return isLatinScript;
    }

    @Override
    public void setIsLatinScript(Boolean isLatinScript) {
        this.isLatinScript = prepareForAssignment(this.isLatinScript, isLatinScript);
    }
}
