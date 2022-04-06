package np.com.rabin.securityclient.controller;

import np.com.rabin.securityclient.entity.UserEntity;
import np.com.rabin.securityclient.event.RegistrationCompleteEvent;
import np.com.rabin.securityclient.model.UserModel;
import np.com.rabin.securityclient.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        UserEntity userEntity = userService.registerUser(userModel);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(userEntity, applicationUrl(request)));
        return "Success";
    }
    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User Verified successfully.";
        }
        return "User verification failed. Verification Token is bad or expired";
    }

    private String applicationUrl(HttpServletRequest request) {

        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}
