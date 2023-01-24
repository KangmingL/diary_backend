package com.kangming.diary_backend;

import com.kangming.diary_backend.post.Post;
import com.kangming.diary_backend.post.PostRepository;
import com.kangming.diary_backend.user.User;
import com.kangming.diary_backend.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PostRepository postRepository){
        return args -> {
            User abby = new User(
                    "Abby",
                    "abby1@umd.edu",
                    LocalDate.of(1999, 2, 11)
            );

            User bob = new User(
                    "Bob",
                    "bob@umd.edu",
                    LocalDate.of(1998, 11, 11)
            );
            User cindy = new User(
                    "cindy_abc",
                    "cindy1@gmail.com",
                    LocalDate.of(1998, 03, 9)
            );

            User jack = new User(
                    "jacccck",
                    "jackcool@gmail.com",
                    LocalDate.of(1999, 11, 1)
            );

            userRepository.saveAll(
                    List.of(abby, bob, cindy, jack)
            );

            Post post1 = new Post("My name is Abby, nice to meet u guys!", new Timestamp(System.currentTimeMillis()), abby);
            Thread.sleep(1000);
            Post post2 = new Post("My name is cindy, wassup!", new Timestamp(System.currentTimeMillis()), cindy);
            Thread.sleep(1000);
            Post post3 = new Post("My name is Jack, HAVE A NICE DAY!", new Timestamp(System.currentTimeMillis()), jack);
            Thread.sleep(1000);
            Post post4 = new Post("Hello World!", new Timestamp(System.currentTimeMillis()), abby);
            postRepository.saveAll(List.of(post1, post2, post3, post4));
        };
    }
}
