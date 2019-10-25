package stubidp.saml.extensions.extensions.versioning.application;

import stubidp.saml.extensions.extensions.StringValueSamlObject;
import stubidp.saml.extensions.extensions.versioning.Version;

import javax.xml.namespace.QName;

public interface ApplicationVersion extends StringValueSamlObject {
    String DEFAULT_ELEMENT_LOCAL_NAME = "ApplicationVersion";

    QName DEFAULT_ELEMENT_NAME = new QName(Version.NAMESPACE_URI, DEFAULT_ELEMENT_LOCAL_NAME, Version.NAMESPACE_PREFIX);
}
