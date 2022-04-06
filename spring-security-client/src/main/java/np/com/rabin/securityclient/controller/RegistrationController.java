package np.com.rabin.securityclient.controller;

import np.com.rabin.securityclient.entity.UserEntity;
import np.com.rabin.securityclient.event.RegistrationCompleteEvent;
import np.com.rabin.securityclient.model.UserModel;
import np.com.rabin.securityclient.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel) {
        UserEntity userEntity = userService.registerUser(userModel);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(userEntity, "url"));
        return "Success";
    }
}
