package com.kangming.diary_backend;

import com.kangming.diary_backend.post.Post;
import com.kangming.diary_backend.post.PostRepository;
import com.kangming.diary_backend.user.User;
import com.kangming.diary_backend.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PostRepository postRepository){
        return args -> {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            User abby = new User(
                    "Abby",
                    "abby1@umd.edu",
                    new Timestamp((dateFormat.parse("02/12/1997").getTime())),
                    true
            );

            User bob = new User(
                    "Bob",
                    "bob@umd.edu",
                    new Timestamp((dateFormat.parse("05/15/1991").getTime())),
                    false
            );
            User cindy = new User(
                    "cindy_abc",
                    "cindy1@gmail.com",
                    new Timestamp((dateFormat.parse("11/14/1967").getTime())),
                    false
            );

            User jack = new User(
                    "jacccck",
                    "jackcool@gmail.com",
                    new Timestamp((dateFormat.parse("10/13/1987").getTime())),
                    false
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
