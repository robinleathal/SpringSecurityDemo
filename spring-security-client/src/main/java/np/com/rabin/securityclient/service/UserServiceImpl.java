package np.com.rabin.securityclient.service;

import np.com.rabin.securityclient.entity.UserEntity;
import np.com.rabin.securityclient.model.UserModel;
import np.com.rabin.securityclient.repo.UserRepository;
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
}
