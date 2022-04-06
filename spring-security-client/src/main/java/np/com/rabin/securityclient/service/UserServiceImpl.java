package np.com.rabin.securityclient.service;

import np.com.rabin.securityclient.entity.UserEntity;
import np.com.rabin.securityclient.entity.VerificationToken;
import np.com.rabin.securityclient.model.UserModel;
import np.com.rabin.securityclient.repo.UserRepository;
import np.com.rabin.securityclient.repo.VerificationTokenRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
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
        
        if (verificationToken.getExpirationDate().getTime() - calendar.getTime().getTime() <=0) {
            verificationTokenRepository.delete(verificationToken);
            return "Token expired";

        }
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        return "Valid";
    }
}
