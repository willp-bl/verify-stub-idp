package stubidp.saml.stubidp.test.transformers;

import javax.inject.Inject;
import org.opensaml.saml.saml2.core.Response;
import stubidp.saml.utils.core.transformers.outbound.decorators.ResponseAssertionSigner;
import stubidp.saml.serializers.serializers.XmlObjectToBase64EncodedStringTransformer;
import stubidp.saml.utils.core.transformers.outbound.decorators.ResponseSignatureCreator;
import stubidp.saml.utils.core.transformers.outbound.decorators.SamlResponseAssertionEncrypter;
import stubidp.saml.utils.core.transformers.outbound.decorators.SamlSignatureSigner;

import java.util.function.Function;

public class StubIdpResponseToStringTransformer implements Function<Response, String> {

    private final XmlObjectToBase64EncodedStringTransformer<Response> xmlObjectToBase64EncodedStringTransformer;
    private final SamlSignatureSigner<Response> samlSignatureSigner;
    private final SamlResponseAssertionEncrypter assertionEncrypter;
    private final ResponseAssertionSigner responseAssertionSigner;
    private final ResponseSignatureCreator signatureCreator;

    @Inject
    public StubIdpResponseToStringTransformer(
            XmlObjectToBase64EncodedStringTransformer<Response> xmlObjectToBase64EncodedStringTransformer,
            SamlSignatureSigner<Response> samlSignatureSigner,
            SamlResponseAssertionEncrypter assertionEncrypter,
            ResponseAssertionSigner responseAssertionSigner,
            ResponseSignatureCreator signatureCreator) {

        this.xmlObjectToBase64EncodedStringTransformer = xmlObjectToBase64EncodedStringTransformer;
        this.samlSignatureSigner = samlSignatureSigner;
        this.assertionEncrypter = assertionEncrypter;
        this.responseAssertionSigner = responseAssertionSigner;
        this.signatureCreator = signatureCreator;
    }

    @Override
    public String apply(Response response) {
        Response responseWithUnsignedSignature = signatureCreator.addUnsignedSignatureTo(response);
        Response responseWithSignedAssertions = responseAssertionSigner.signAssertions(responseWithUnsignedSignature);
        Response responseWithEncryptedAssertions = assertionEncrypter.encryptAssertions(responseWithSignedAssertions);
        Response signedResponse = samlSignatureSigner.sign(responseWithEncryptedAssertions);
        return xmlObjectToBase64EncodedStringTransformer.apply(signedResponse);
    }

}
