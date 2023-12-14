package com.Shift.Shiftii.shift;

import com.Shift.Shiftii.shops.Shop;
import com.Shift.Shiftii.shops.ShopRepository;
import com.Shift.Shiftii.user.User;
import com.Shift.Shiftii.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class ShiftConfig {

    @Bean
    CommandLineRunner shiftCommandLineRunner(ShiftRepository shiftRepository) {
        return args -> {
            // TODO: 08/12/2023
            System.out.println("Saved!");
        };
    }



}
