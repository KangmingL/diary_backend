package com.kangming.diary_backend.post;

public class PostUtils {
    private PostUtils(){}

    public static boolean postExists(PostRepository repo, Long postId){
        if(!repo.existsById(postId)){
            throw new IllegalStateException("post with id "+ postId + " does not exist!");
        }
        return true;
    }
}
