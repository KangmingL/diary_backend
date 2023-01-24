package com.kangming.diary_backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    // ******************* USERS MANAGEMENT *******************
    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping(path = "{userId}")
    public User getUserById(@PathVariable Long userId) {return userService.getUserById(userId); }

    @PostMapping
    public ResponseEntity<User> registerNewUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @DeleteMapping(path = "{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable("userId") Long userId){
        return userService.deleteUser(userId);
    }

    @PutMapping(path = "{userId}")
    public void updateUser(
        @PathVariable("userId") Long userId,
        @RequestParam(required = false) String userName,
        @RequestParam(required = false) String email
    ){
        userService.updateUser(userId, userName, email);
    }


// ******************* FRIENDSHIP MANAGEMENT *******************

//    @GetMapping(path = "{userId}/following")
//    public void getFollowing(
//            @PathVariable("userId") Long userId
//    ){
//        userService.getFollowing(userId);
//    }
}
