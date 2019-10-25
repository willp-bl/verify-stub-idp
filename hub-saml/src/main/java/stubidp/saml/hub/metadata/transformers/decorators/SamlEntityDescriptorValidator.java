package stubidp.saml.hub.metadata.transformers.decorators;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import stubidp.saml.extensions.validation.SamlTransformationErrorException;
import stubidp.saml.extensions.validation.SamlValidationSpecificationFailure;
import stubidp.saml.hub.core.errors.SamlTransformationErrorFactory;

public class SamlEntityDescriptorValidator {

    public void validate(EntityDescriptor descriptor) {
        if (Strings.isNullOrEmpty(descriptor.getEntityID())) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingOrEmptyEntityID();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        if (descriptor.getCacheDuration() == null && descriptor.getValidUntil() == null) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingCacheDurationAndValidUntil();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        validateRoleDescriptor(descriptor);
    }

    private void validateRoleDescriptor(EntityDescriptor descriptor) {
        if (descriptor.getRoleDescriptors().isEmpty()) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingRoleDescriptor();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        RoleDescriptor roleDescriptor = descriptor.getRoleDescriptors().get(0);

        if (roleDescriptor.getKeyDescriptors().isEmpty()) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingKeyDescriptor();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        KeyInfo keyInfo = roleDescriptor.getKeyDescriptors().get(0).getKeyInfo();
        if (keyInfo == null) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingKeyInfo();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
        if (keyInfo.getX509Datas().isEmpty()) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingX509Data();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        if (x509Data.getX509Certificates().isEmpty()) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingX509Certificate();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        X509Certificate x509Certificate = x509Data.getX509Certificates().get(0);
        if (StringUtils.isEmpty(x509Certificate.getValue())) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.emptyX509Certificiate();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
    }

}
