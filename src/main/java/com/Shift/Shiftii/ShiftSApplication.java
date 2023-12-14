package com.Shift.Shiftii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@SpringBootApplication
public class ShiftSApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiftSApplication.class, args);
        testTime();
    }


    private static void testTime() {
        final LocalDateTime currentDateTime = LocalDateTime.now();
        // Define a custom date-time formatter
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime using the formatter
        final String formattedDateTime = currentDateTime.format(formatter);
        // Print the formatted date and time

        final LocalDate today = LocalDate.now();
        System.out.println("Today: " + today);

        System.out.println("Original LocalDateTime: " + formattedDateTime);
        LocalDateTime truncatedDateTime = currentDateTime.withNano((currentDateTime.getNano() / 1_000_0000) * 1_000_00);
        System.out.println("truncatedDateTime Format: " + truncatedDateTime);

    }


    @GetMapping("/")
    public String greetings() {

        return "Nothing for now :)";
    }
}
