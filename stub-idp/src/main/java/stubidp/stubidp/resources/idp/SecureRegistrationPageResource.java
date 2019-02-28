package stubidp.stubidp.resources.idp;

import stubidp.stubidp.repositories.IdpSessionRepository;
import stubidp.stubidp.repositories.IdpStubsRepository;
import stubidp.stubidp.views.SamlResponseRedirectViewFactory;
import stubidp.stubidp.filters.SessionCookieValueMustExistAsASession;
import stubidp.stubidp.services.IdpUserService;
import stubidp.stubidp.services.NonSuccessAuthnResponseService;

import javax.inject.Inject;

@SessionCookieValueMustExistAsASession
public class SecureRegistrationPageResource extends RegistrationPageResource {

    @Inject
    public SecureRegistrationPageResource(IdpStubsRepository idpStubsRepository,
                                          IdpUserService idpUserService,
                                          SamlResponseRedirectViewFactory samlResponseRedirectViewFactory,
                                          NonSuccessAuthnResponseService nonSuccessAuthnResponseService,
                                          IdpSessionRepository idpSessionRepository) {
        super(idpStubsRepository, idpUserService, samlResponseRedirectViewFactory, nonSuccessAuthnResponseService, idpSessionRepository);
    }
}
