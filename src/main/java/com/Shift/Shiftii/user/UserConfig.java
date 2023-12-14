package com.Shift.Shiftii.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner userCommandLineRunner(UserRepository userRepository) {
        return args -> {
            // TODO: 08/12/2023
            User user1 = new User("alice@gmail.com","Alice Laice");
            User user2 = new User("trine@gmail.com","Trine Trein");

            userRepository.saveAll(List.of(user1,user2));
            // TODO: 10/12/2023 uncomment this at the end!
//            userRepository.saveAll(userRepository.findAll());
        };
    }
}
