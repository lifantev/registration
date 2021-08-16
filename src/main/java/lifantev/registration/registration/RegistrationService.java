package lifantev.registration.registration;

import lifantev.registration.user.AppUser;
import lifantev.registration.user.AppUserRole;
import lifantev.registration.user.AppUserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final AppUserService appUserService;
    private final static EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    public RegistrationService(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    public String register(RegistrationRequest request) {
        boolean isValid = EMAIL_VALIDATOR.isValid(request.getEmail());

        if (!isValid) {
            throw new IllegalStateException("Email not valid");
        }

        return appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER
                )
        );
    }
}
