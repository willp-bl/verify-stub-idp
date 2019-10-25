@SuppressWarnings({"requires-automatic", "requires-transitive-automatic"})
module stubidp.saml.serializers {
    exports stubidp.saml.serializers.deserializers.validators;
    exports stubidp.saml.serializers.serializers;
    exports stubidp.saml.serializers.deserializers;
    exports stubidp.saml.serializers.deserializers.parser;

    requires stubidp.saml.extensions;
    requires org.apache.commons.codec;

    requires transitive org.opensaml.core;
    requires transitive net.shibboleth.utilities.java.support;
    requires transitive java.xml;
}