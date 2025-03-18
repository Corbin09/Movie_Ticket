package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.UserDTO;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser (UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setUserImg(userDTO.getUserImg());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setSex(userDTO.getSex());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setRole(userDTO.getRole());
        user.setResetToken(userDTO.getResetToken());
        user.setResetTokenExpire(userDTO.getResetTokenExpire());
        user.setStatus(userDTO.getStatus());
        return userRepository.save(user);
    }

    public User updateUser (Long id, UserDTO userDTO) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User user = userData.get();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setUserImg(userDTO.getUserImg());
            user.setEmail(userDTO.getEmail());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setSex(userDTO.getSex());
            user.setDateOfBirth(userDTO.getDateOfBirth());
            user.setRole(userDTO.getRole());
            user.setResetToken(userDTO.getResetToken());
            user.setResetTokenExpire(userDTO.getResetTokenExpire());
            user.setStatus(userDTO.getStatus());
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteUser (Long id) {
        userRepository.deleteById(id);
    }
}