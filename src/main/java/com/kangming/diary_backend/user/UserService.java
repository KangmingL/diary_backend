package com.kangming.diary_backend.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * UserService --- User service class implements backend functionalities.
 * @author Kangming Luo
 * */
@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Get all the users in DB. Useful during testing.
     * @return response entity contains a list of all users' info.
     * */
    public ResponseEntity<List<User>> getUsers(){
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    /**
     * Get user entity by user id.
     * @param userId user id.
     * @return response entity include user info or null if not exists.
     * */
    public ResponseEntity<User> getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else{
            return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
        }
    }

    /**
     * Create and add a new user to DB.
     * @param user new user entity.
     * @return response entity with message.
     * */
    public ResponseEntity<String> addUser(User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail());
        if(userOptional.isPresent()){
            return new ResponseEntity<>("Email was taken!", HttpStatus.CONFLICT);
        }
        userRepository.save(user);
        return new ResponseEntity<>("Successfully create a new user!", HttpStatus.CREATED);
    }

    /**
     * Delete user by id.
     * @param userId user id.
     * @return response entity indicates result.
     * */
    public ResponseEntity<String> deleteUser(Long userId) {
        if(!userRepository.existsById(userId)){
            return new ResponseEntity<>("No user with user id "+userId+" found in DB!", HttpStatus.BAD_REQUEST);
        }
        userRepository.deleteById(userId);
        return new ResponseEntity<>("User with user id "+userId+" was deleted successfully!", HttpStatus.OK);
    }

    /**
     * Update user info.
     * @param userId user id
     * @param userName new username
     * @param email new email
     * */
    @Transactional
    public ResponseEntity<String> updateUser(Long userId, String userName, String email, String password) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return new ResponseEntity<>("User with user id "+userId+" not found in DB!", HttpStatus.BAD_REQUEST);
        }
        User user = userOptional.get();
        if(userName != null && userName.length() != 0 && !Objects.equals(userName, user.getUserName())){
            user.setUserName(userName);
        }else{
            return new ResponseEntity<>("Username had been taken!", HttpStatus.BAD_REQUEST);
        }

        if(email != null && email.length() != 0 && !Objects.equals(email, user.getEmail())){
            if(!userRepository.existsUserByEmail(email)){
                return new ResponseEntity<>("Email had been taken!", HttpStatus.BAD_REQUEST);
            }
            user.setEmail(email);
        }
        user.setPassword(password);
        return new ResponseEntity<>("Successfully update user info!", HttpStatus.OK);
    }
}
