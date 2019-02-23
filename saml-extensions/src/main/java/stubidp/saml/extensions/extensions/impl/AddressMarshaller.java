package stubidp.saml.extensions.extensions.impl;

import stubidp.saml.extensions.extensions.Address;

public class AddressMarshaller extends BaseMdsSamlObjectMarshaller {
    public AddressMarshaller(){
        super(Address.TYPE_LOCAL_NAME);
    }
}
