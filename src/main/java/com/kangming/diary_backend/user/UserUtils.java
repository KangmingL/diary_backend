package com.kangming.diary_backend.user;

public class UserUtils {
    private UserUtils(){}
    public static User getUserUtil(UserRepository repo, Long id){
        User user = repo.findById(id).orElseThrow(
                () -> new IllegalStateException("user with id "+id+" does not exists!")
        );
        return user;
    }
}
