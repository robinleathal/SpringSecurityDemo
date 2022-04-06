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
        userEntity.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userEntity.setRole("USER");

        BeanUtils.copyProperties(userModel, userEntity);
        userRepository.save(userEntity);
        return userEntity;
    }

    @Override
    public void saveVerificationTokenForUser(String token, UserEntity userEntity) {
        VerificationToken verificationToken = new VerificationToken(userEntity, token);
        verificationTokenRepository.save(verificationToken);
    }
}
