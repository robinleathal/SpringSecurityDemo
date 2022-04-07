package np.com.rabin.securityclient.controller;

import lombok.extern.slf4j.Slf4j;
import np.com.rabin.securityclient.entity.UserEntity;
import np.com.rabin.securityclient.entity.VerificationToken;
import np.com.rabin.securityclient.event.RegistrationCompleteEvent;
import np.com.rabin.securityclient.model.PasswordModel;
import np.com.rabin.securityclient.model.UserModel;
import np.com.rabin.securityclient.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {

    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel) {
        UserEntity userEntity = userService.findUserByEmail(passwordModel.getEmail());
        //check old password
        if (!userService.checkIfValidOldPassword(userEntity, passwordModel.getOldPassword())) {
            return "Invalid Old Password.";
        }
        //save New Password
        userService.changePassword(userEntity, passwordModel.getNewPassword());
        return "Password changed successfully.";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        UserEntity userEntity = userService.findUserByEmail(passwordModel.getEmail());
        String url ="";
        if (userEntity != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetToken(userEntity, token);
            url = passwordResetTokenMail(userEntity, applicationUrl(request), token);
        }
        return url;
    }

    private String passwordResetTokenMail(UserEntity userEntity, String applicationUrl, String token) {
        //send mail to user with verification link and token
        String url = applicationUrl+"/savePassword?token="+token;
        //Send Verification mail
        log.info("Click the verification link to reset your password: {}", url);
        return url;
    }
    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid reset Token1.";
        }
        Optional<UserEntity> userEntity = userService.getUserByPasswordResetToken(token);
        if (userEntity.isPresent()) {
            userService.changePassword(userEntity.get(), passwordModel.getNewPassword());
            return "Password reset successful.";
        } else {
            return "Invalid reset Token.";
        }
        //return token;
    }

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
    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        UserEntity userEntity = verificationToken.getUserEntity();
        resendVerificationTokenMail(userEntity, applicationUrl(request), verificationToken);
        return "Verification Link resent. ";
    }

    private void resendVerificationTokenMail(UserEntity userEntity, String applicationUrl, VerificationToken verificationToken) {
        //send mail to user with verification link and token
        String url = applicationUrl+"/verifyRegistration?token="+verificationToken.getToken();
        //Send Verification mail
        log.info("Click the verification link to verify your new account: {}", url);
    }

    private String applicationUrl(HttpServletRequest request) {

        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }


}
