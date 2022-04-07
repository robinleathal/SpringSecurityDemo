package np.com.rabin.securityclient.service;

import np.com.rabin.securityclient.entity.PasswordResetToken;
import np.com.rabin.securityclient.entity.UserEntity;
import np.com.rabin.securityclient.entity.VerificationToken;
import np.com.rabin.securityclient.model.UserModel;
import np.com.rabin.securityclient.repo.PasswordResetTokenRepository;
import np.com.rabin.securityclient.repo.UserRepository;
import np.com.rabin.securityclient.repo.VerificationTokenRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Override
    public UserEntity registerUser(UserModel userModel) {

        UserEntity userEntity = new UserEntity();
        //userEntity.setEmail(userModel.getEmail());


//        userEntity.setFirstName(userModel.getFirstName());
//        userEntity.setLastName(userModel.getLastName());
//        userEntity.setEmail(userModel.getEmail());
//
//        userEntity.setUsername(userModel.getUsername());



        BeanUtils.copyProperties(userModel, userEntity);
        userEntity.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userEntity.setRole("USER");
        userRepository.save(userEntity);
        return userEntity;
    }

    @Override
    public void saveVerificationTokenForUser(String token, UserEntity userEntity) {
        VerificationToken verificationToken = new VerificationToken(userEntity, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "Invalid token";
        }
        UserEntity userEntity = verificationToken.getUserEntity();
        Calendar calendar = Calendar.getInstance();

        if ((verificationToken.getExpirationDate().getTime() - calendar.getTime().getTime()) <=0) {
            verificationTokenRepository.delete(verificationToken);
            return "Token expired";

        }
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        return "Valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetToken(UserEntity userEntity, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(userEntity, token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            return "Invalid token";
        }
        UserEntity userEntity = passwordResetToken.getUserEntity();
        Calendar calendar = Calendar.getInstance();

        if ((passwordResetToken.getExpirationDate().getTime() - calendar.getTime().getTime()) <=0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "Token expired";

        }

        return "valid";
    }

    @Override
    public Optional<UserEntity> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUserEntity());

    }

    @Override
    public void changePassword(UserEntity userEntity, String newPassword) {
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    @Override
    public boolean checkIfValidOldPassword(UserEntity userEntity, String oldPassword) {

        return passwordEncoder.matches(oldPassword, userEntity.getPassword());
    }
}
