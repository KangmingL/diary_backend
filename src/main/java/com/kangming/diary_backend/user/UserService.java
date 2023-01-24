package com.kangming.diary_backend.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.kangming.diary_backend.user.UserUtils.getUserUtil;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // ******************** User Management ***********************
    public List<User> getUsers(){return userRepository.findAll();}

    public User getUserById(Long userId) {
        return getUserUtil(userRepository, userId);
    }
    public ResponseEntity<User> addUser(User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail());
        if(userOptional.isPresent()){
            throw new IllegalStateException("email taken");
        }
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    public ResponseEntity<User> deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("user with id "+userId+" does not exists")

        );
        userRepository.deleteById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<User> updateUser(Long userId, String userName, String email) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new IllegalStateException("user with id "+userId+" does not exists!")
        );
        if(userName != null && userName.length() != 0 && !Objects.equals(userName, user.getUserName())){
            user.setUserName(userName);
        }else{
            throw new IllegalStateException("Invalid username!");
        }

        if(email != null && email.length() != 0 && !Objects.equals(email, user.getEmail())){
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if(userOptional.isPresent()){
                throw new IllegalStateException("email taken!");
            }
            user.setEmail(email);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
