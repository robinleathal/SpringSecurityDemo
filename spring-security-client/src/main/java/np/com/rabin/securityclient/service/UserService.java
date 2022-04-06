package np.com.rabin.securityclient.service;

import np.com.rabin.securityclient.entity.UserEntity;
import np.com.rabin.securityclient.model.UserModel;

public interface UserService {

    UserEntity registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, UserEntity userEntity);

    String validateVerificationToken(String token);
}
