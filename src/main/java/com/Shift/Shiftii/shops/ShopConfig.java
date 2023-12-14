package com.Shift.Shiftii.shops;

import com.Shift.Shiftii.user.User;
import com.Shift.Shiftii.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ShopConfig {

    @Bean
    CommandLineRunner shopCommandLineRunner(ShopRepository shopRepository) {
        return args -> {

            // TODO: 08/12/2023 simply adding few shops auto for testing!
            final Shop shop1 = new Shop("Electric shop");
            final Shop shop2 = new Shop("Kitchen shop");
            final Shop shop3 = new Shop("Home shop");
            final Shop shop4 = new Shop("Bikes shop");
            final Shop shop5 = new Shop("Kids shop");
            final Shop shop6 = new Shop("Pets shop");
            final Shop shop7 = new Shop("Smart shop");

            shopRepository.saveAll(List.of(shop1,shop2,shop3,shop4,shop5,shop6,shop7));
        };
    }
}
