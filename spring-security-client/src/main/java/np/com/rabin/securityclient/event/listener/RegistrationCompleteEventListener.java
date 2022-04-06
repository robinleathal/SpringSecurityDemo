package np.com.rabin.securityclient.event.listener;

import lombok.extern.slf4j.Slf4j;
import np.com.rabin.securityclient.entity.UserEntity;
import np.com.rabin.securityclient.event.RegistrationCompleteEvent;
import np.com.rabin.securityclient.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.UUID;
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private UserService userService;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        //Create the verification token for user with a link
        UserEntity userEntity = event.getUserEntity();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, userEntity);
        //send mail to user with verification link and token
        String url = event.getApplicationUrl()+"verifyRegistration?token="+token;
        //Send Verification mail
        log.info("Click the verification link to verify your new account: {}", url);
    }
}
