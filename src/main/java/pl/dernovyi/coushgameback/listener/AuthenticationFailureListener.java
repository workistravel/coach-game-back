package pl.dernovyi.coushgameback.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import pl.dernovyi.coushgameback.service.impl.LoginAttemptService;

@Component
public class AuthenticationFailureListener {
    private LoginAttemptService loginAttemptService;
    @Autowired
    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String){
            String email = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(email);

        }

    }
}
