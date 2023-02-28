package com.kangming.diary_backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * UserController --- Controller class that is used to handle REST request related to users.
 * @author Kangming Luo
 * */
@RestController
@RequestMapping(path = "api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    // ****************** GET ******************
    @GetMapping
    public ResponseEntity<List<User>> getUsers(){
        return userService.getUsers();
    }

    @GetMapping(path = "{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    // ****************** POST ******************
    @PostMapping
    public ResponseEntity<String> registerNewUser(@RequestBody User user){
        return userService.addUser(user);
    }

    // ****************** DELETE ******************
    @DeleteMapping(path = "{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId){
        return userService.deleteUser(userId);
    }

    // ****************** PUT ******************
    @PutMapping(path = "{userId}")
    public ResponseEntity<String> updateUser(
        @PathVariable("userId") Long userId,
        @RequestParam(required = false) String userName,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String password
    ){
        return userService.updateUser(userId, userName, email, password);
    }
}
