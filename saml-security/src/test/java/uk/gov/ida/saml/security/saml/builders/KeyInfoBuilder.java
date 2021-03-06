package uk.gov.ida.saml.security.saml.builders;

import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.impl.KeyNameBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class KeyInfoBuilder {

    private List<X509Data> x509Datas = new ArrayList<>();
    private Optional<String> keyName = Optional.ofNullable("default-key-name");

    public static KeyInfoBuilder aKeyInfo() {
        return new KeyInfoBuilder();
    }

    public KeyInfo build() {
        KeyInfo keyInfo = new org.opensaml.xmlsec.signature.impl.KeyInfoBuilder().buildObject();

        KeyName keyNameValue = createKeyName(keyName.get());
        keyInfo.getKeyNames().add(keyNameValue);

        x509Datas.forEach(keyInfo.getX509Datas()::add);

        return keyInfo;
    }

    public KeyInfoBuilder withX509Data(X509Data x509Data) {
        this.x509Datas = Arrays.asList(x509Data);
        return this;
    }

    private KeyName createKeyName(String keyNameValue) {
        final KeyName keyName = new KeyNameBuilder().buildObject();
        keyName.setValue(keyNameValue);
        return keyName;
    }

    public KeyInfoBuilder withKeyName(String keyNameValue) {
        keyName = Optional.ofNullable(keyNameValue);
        return this;
    }
}
