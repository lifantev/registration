package lifantev.registration.registration;

import lifantev.registration.registration.token.ConfirmationToken;
import lifantev.registration.registration.token.ConfirmationTokenService;
import lifantev.registration.user.AppUser;
import lifantev.registration.user.AppUserRole;
import lifantev.registration.user.AppUserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrationService {

    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final static EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    public RegistrationService(AppUserService appUserService, ConfirmationTokenService confirmationTokenService) {
        this.appUserService = appUserService;
        this.confirmationTokenService = confirmationTokenService;
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

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiresAt = confirmationToken.getExpiresAt();

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }
}
