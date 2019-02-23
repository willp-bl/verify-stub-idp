package uk.gov.ida.saml.security.saml;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSString;
import stubidp.saml.extensions.extensions.Address;
import stubidp.saml.extensions.extensions.Date;
import stubidp.saml.extensions.extensions.Gender;
import stubidp.saml.extensions.extensions.Gpg45Status;
import stubidp.saml.extensions.extensions.IPAddress;
import stubidp.saml.extensions.extensions.IdpFraudEventId;
import stubidp.saml.extensions.extensions.InternationalPostCode;
import stubidp.saml.extensions.extensions.Line;
import stubidp.saml.extensions.extensions.PersonName;
import stubidp.saml.extensions.extensions.PostCode;
import stubidp.saml.extensions.extensions.StatusValue;
import stubidp.saml.extensions.extensions.StringBasedMdsAttributeValue;
import stubidp.saml.extensions.extensions.UPRN;
import stubidp.saml.extensions.extensions.Verified;
import stubidp.saml.extensions.extensions.impl.AddressBuilder;
import stubidp.saml.extensions.extensions.impl.AddressMarshaller;
import stubidp.saml.extensions.extensions.impl.AddressUnmarshaller;
import stubidp.saml.extensions.extensions.impl.DateBuilder;
import stubidp.saml.extensions.extensions.impl.DateImpl;
import stubidp.saml.extensions.extensions.impl.GenderBuilder;
import stubidp.saml.extensions.extensions.impl.GenderImpl;
import stubidp.saml.extensions.extensions.impl.Gpg45StatusBuilder;
import stubidp.saml.extensions.extensions.impl.Gpg45StatusImpl;
import stubidp.saml.extensions.extensions.impl.IPAddressBuilder;
import stubidp.saml.extensions.extensions.impl.IPAddressImpl;
import stubidp.saml.extensions.extensions.impl.IdpFraudEventIdBuilder;
import stubidp.saml.extensions.extensions.impl.IdpFraudEventIdImpl;
import stubidp.saml.extensions.extensions.impl.InternationalPostCodeBuilder;
import stubidp.saml.extensions.extensions.impl.LineBuilder;
import stubidp.saml.extensions.extensions.impl.PersonNameBuilder;
import stubidp.saml.extensions.extensions.impl.PersonNameImpl;
import stubidp.saml.extensions.extensions.impl.PostCodeBuilder;
import stubidp.saml.extensions.extensions.impl.StatusValueBuilder;
import stubidp.saml.extensions.extensions.impl.StatusValueImpl;
import stubidp.saml.extensions.extensions.impl.StringBasedMdsAttributeValueBuilder;
import stubidp.saml.extensions.extensions.impl.StringBasedMdsAttributeValueImpl;
import stubidp.saml.extensions.extensions.impl.StringValueSamlObjectImpl;
import stubidp.saml.extensions.extensions.impl.UPRNBuilder;
import stubidp.saml.extensions.extensions.impl.VerifiedBuilder;
import stubidp.saml.extensions.extensions.impl.VerifiedImpl;

public abstract class IdaSamlBootstrap {

    private static boolean hasBeenBootstrapped = false;

    public static class BootstrapException extends RuntimeException{
        public BootstrapException(Exception e){
            super(e);
        }
    }

    public static synchronized void bootstrap() {
        if (hasBeenBootstrapped) {
            return;
        }

        try {
            doBootstrapping();
        } catch (InitializationException e) {
            throw new BootstrapException(e);
        }

        hasBeenBootstrapped = true;
    }

    private static void doBootstrapping() throws InitializationException {
        InitializationService.initialize();

        //HACK: Why is the string type even registered? It can't ever get the element name right in that case, can it? [Mark/Peter 15/1/2013]
        XMLObjectProviderRegistrySupport.deregisterObjectProvider(XSString.TYPE_NAME);

        XMLObjectProviderRegistrySupport.registerObjectProvider(PersonName.TYPE_NAME, new PersonNameBuilder(), PersonNameImpl.MARSHALLER, PersonNameImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Date.TYPE_NAME, new DateBuilder(), DateImpl.MARSHALLER, DateImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Address.TYPE_NAME, new AddressBuilder(), new AddressMarshaller(), new AddressUnmarshaller());
        XMLObjectProviderRegistrySupport.registerObjectProvider(PostCode.DEFAULT_ELEMENT_NAME, new PostCodeBuilder(), StringValueSamlObjectImpl.MARSHALLER, StringValueSamlObjectImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(InternationalPostCode.DEFAULT_ELEMENT_NAME, new InternationalPostCodeBuilder(), StringValueSamlObjectImpl.MARSHALLER, StringValueSamlObjectImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(UPRN.DEFAULT_ELEMENT_NAME, new UPRNBuilder(), StringValueSamlObjectImpl.MARSHALLER, StringValueSamlObjectImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Line.DEFAULT_ELEMENT_NAME, new LineBuilder(), StringValueSamlObjectImpl.MARSHALLER, StringValueSamlObjectImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(StringBasedMdsAttributeValue.TYPE_NAME, new StringBasedMdsAttributeValueBuilder(), StringBasedMdsAttributeValueImpl.MARSHALLER, StringBasedMdsAttributeValueImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(StringBasedMdsAttributeValue.DEFAULT_ELEMENT_NAME, new StringBasedMdsAttributeValueBuilder(), StringBasedMdsAttributeValueImpl.MARSHALLER, StringBasedMdsAttributeValueImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Gender.TYPE_NAME, new GenderBuilder(), GenderImpl.MARSHALLER, GenderImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(IdpFraudEventId.TYPE_NAME, new IdpFraudEventIdBuilder(), IdpFraudEventIdImpl.MARSHALLER, IdpFraudEventIdImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Gpg45Status.TYPE_NAME, new Gpg45StatusBuilder(), Gpg45StatusImpl.MARSHALLER, IdpFraudEventIdImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(IPAddress.TYPE_NAME, new IPAddressBuilder(), IPAddressImpl.MARSHALLER, IPAddressImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Verified.TYPE_NAME, new VerifiedBuilder(), VerifiedImpl.MARSHALLER, VerifiedImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(StatusValue.DEFAULT_ELEMENT_NAME, new StatusValueBuilder(), StatusValueImpl.MARSHALLER, StatusValueImpl.UNMARSHALLER);
    }
}
