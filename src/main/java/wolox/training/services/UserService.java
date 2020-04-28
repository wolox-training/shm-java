package wolox.training.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User updateUser(User user, User modifiedUser) {
        user.setUserName(modifiedUser.getUserName());
        user.setName(modifiedUser.getName());
        user.setBirthDate(modifiedUser.getBirthDate());
        return userRepository.save(user);
    }

    public User updateUserPassword(User user, String password) {
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }
}
