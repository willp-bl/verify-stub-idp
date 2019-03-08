package stubidp.saml.hub.metadata.transformers.decorators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import stubidp.saml.hub.metadata.transformers.decorators.SamlEntityDescriptorValidator;
import stubidp.saml.utils.core.test.OpenSAMLMockitoRunner;
import stubidp.saml.utils.core.test.SamlTransformationErrorManagerTestHelper;
import stubidp.saml.utils.core.test.builders.metadata.IdpSsoDescriptorBuilder;
import stubidp.saml.utils.core.test.builders.metadata.KeyDescriptorBuilder;
import stubidp.saml.utils.core.test.builders.metadata.KeyInfoBuilder;
import stubidp.saml.utils.core.test.builders.metadata.X509CertificateBuilder;
import stubidp.saml.extensions.validation.SamlValidationSpecificationFailure;
import stubidp.saml.hub.core.errors.SamlTransformationErrorFactory;
import static stubidp.saml.utils.core.test.builders.metadata.EndpointBuilder.anEndpoint;
import static stubidp.saml.utils.core.test.builders.metadata.EntityDescriptorBuilder.anEntityDescriptor;
import static stubidp.saml.utils.core.test.builders.metadata.X509DataBuilder.aX509Data;

@RunWith(OpenSAMLMockitoRunner.class)
public class SamlEntityDescriptorValidatorTest {

    private SamlEntityDescriptorValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new SamlEntityDescriptorValidator();
    }

    @Test
    public void decorate_shouldThrowExceptionWhenEntityIdIsMissing() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withEntityId(null).build();

        assertExceptionMessage(entityDescriptor, SamlTransformationErrorFactory.missingOrEmptyEntityID());

    }

    @Test
    public void decorate_shouldThrowExceptionWhenEntityIdIsEmpty() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withEntityId("").build();

        assertExceptionMessage(entityDescriptor, SamlTransformationErrorFactory.missingOrEmptyEntityID());

    }

    @Test
    public void decorate_shouldThrowExceptionWhenRoleDescriptorDoesNotHaveAKeyDescriptorElement() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withIdpSsoDescriptor(IdpSsoDescriptorBuilder.anIdpSsoDescriptor().withoutDefaultSigningKey().build()).build();

        assertExceptionMessage(entityDescriptor, SamlTransformationErrorFactory.missingKeyDescriptor());

    }

    @Test
    public void decorate_shouldThrowExceptionWhenRoleDescriptorDoesNotHaveAKeyInfoElement() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withIdpSsoDescriptor(IdpSsoDescriptorBuilder.anIdpSsoDescriptor().withoutDefaultSigningKey().addKeyDescriptor(KeyDescriptorBuilder.aKeyDescriptor().withKeyInfo(null).build()).build()).build();

        assertExceptionMessage(entityDescriptor, SamlTransformationErrorFactory.missingKeyInfo());

    }

    @Test
    public void decorate_shouldThrowExceptionWhenRoleDescriptorDoesNotHaveAX509DataElement() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withIdpSsoDescriptor(IdpSsoDescriptorBuilder.anIdpSsoDescriptor().withoutDefaultSigningKey().addKeyDescriptor(KeyDescriptorBuilder.aKeyDescriptor().withKeyInfo(KeyInfoBuilder.aKeyInfo().withX509Data(null).build()).build()).build()).build();

        assertExceptionMessage(entityDescriptor, SamlTransformationErrorFactory.missingX509Data());

    }

    @Test
    public void decorate_shouldThrowExceptionWhenRoleDescriptorDoesNotHaveAX509CertificateElement() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withIdpSsoDescriptor(IdpSsoDescriptorBuilder.anIdpSsoDescriptor().withoutDefaultSigningKey().addKeyDescriptor(KeyDescriptorBuilder.aKeyDescriptor().withKeyInfo(KeyInfoBuilder.aKeyInfo().withX509Data(aX509Data().withX509Certificate(null).build()).build()).build()).build()).build();

        assertExceptionMessage(entityDescriptor, SamlTransformationErrorFactory.missingX509Certificate());

    }

    @Test
    public void decorate_shouldThrowExceptionWhenX509CertificateElementIsEmpty() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withIdpSsoDescriptor(IdpSsoDescriptorBuilder.anIdpSsoDescriptor().withoutDefaultSigningKey().addKeyDescriptor(KeyDescriptorBuilder.aKeyDescriptor().withKeyInfo(KeyInfoBuilder.aKeyInfo().withX509Data(aX509Data().withX509Certificate(X509CertificateBuilder.aX509Certificate().withCertForEntityId(null).withCert(null).build()).build()).build()).build()).build()).build();

        assertExceptionMessage(entityDescriptor, SamlTransformationErrorFactory.emptyX509Certificiate());

    }

    @Test
    public void decorate_shouldThrowExceptionWhenBothValidUntilAndCacheDurationAreMissing() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withValidUntil(null).withCacheDuration(null).build();

        assertExceptionMessage(entityDescriptor, SamlTransformationErrorFactory.missingCacheDurationAndValidUntil());
    }

    @Test
    public void decorate_shouldDoNothingWhenEntityDescriptorIsValid() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withIdpSsoDescriptor(IdpSsoDescriptorBuilder.anIdpSsoDescriptor().withSingleSignOnService(anEndpoint().buildSingleSignOnService()).build()).build();

        validator.validate(entityDescriptor);
    }

    @Test
    public void decorate_shouldNotThrowExceptionWhenEntityDescriptorIsNotSignedButNotRequired() throws Exception {
        EntityDescriptor entityDescriptor = anEntityDescriptor().withoutSigning().build();

        validator.validate(entityDescriptor);

    }

    public void assertExceptionMessage(final EntityDescriptor entityDescriptor, SamlValidationSpecificationFailure failure) {

        SamlTransformationErrorManagerTestHelper.validateFail(
                new SamlTransformationErrorManagerTestHelper.Action() {
                    @Override
                    public void execute() {
                        validator.validate(entityDescriptor);
                    }
                },
                failure
        );

    }
}