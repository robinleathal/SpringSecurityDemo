package np.com.rabin.securityclient.service;

import np.com.rabin.securityclient.entity.UserEntity;
import np.com.rabin.securityclient.entity.VerificationToken;
import np.com.rabin.securityclient.model.UserModel;

import java.util.Optional;

public interface UserService {

    UserEntity registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, UserEntity userEntity);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    UserEntity findUserByEmail(String email);

    void createPasswordResetToken(UserEntity userEntity, String token);

    String validatePasswordResetToken(String token);

    Optional<UserEntity> getUserByPasswordResetToken(String token);

    void changePassword(UserEntity userEntity, String newPassword);

    boolean checkIfValidOldPassword(UserEntity userEntity, String oldPassword);
}
