@SuppressWarnings({"requires-automatic", "requires-transitive-automatic"})
module stubidp.saml.security {
    exports stubidp.saml.security;
    exports stubidp.saml.security.validators;
    exports stubidp.saml.security.validators.signature;
    exports stubidp.saml.security.validators.encryptedelementtype;
    exports stubidp.saml.security.validators.issuer;
    exports stubidp.saml.security.signature;

    opens stubidp.saml.security; // for tests
    opens stubidp.saml.security.validators.signature; // for tests

    requires com.google.common;
    requires org.slf4j;
    requires xmlsec;
    requires jsr305; // https://github.com/google/guava/issues/2960
    requires org.opensaml.core;

    requires transitive org.opensaml.xmlsec.impl;
    requires transitive org.joda.time;
    requires transitive org.opensaml.security;
    requires transitive org.opensaml.security.impl;
    requires transitive org.opensaml.xmlsec;
    requires transitive org.opensaml.saml;
    requires transitive java.xml;
    requires transitive java.validation;
    requires transitive org.opensaml.saml.impl;
    requires transitive net.shibboleth.utilities.java.support;
    requires transitive stubidp.saml.extensions;
    requires transitive stubidp.security.utils;
}