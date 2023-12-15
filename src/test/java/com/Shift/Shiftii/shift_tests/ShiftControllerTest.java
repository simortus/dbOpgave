package com.Shift.Shiftii.shift_tests;

import com.Shift.Shiftii.shift.Shift;
import com.Shift.Shiftii.shift.ShiftControleur;
import com.Shift.Shiftii.shops.Shop;
import com.Shift.Shiftii.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
public class ShiftControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ShiftControleur shiftControleur;


    /**
     * General test to ensure a proper context loading of the shift controller
     * Test passed:1 √
     **/
    @Test
    void contextLoads() {
        assertThat(this.shiftControleur).isNotNull();
    }


    /**
     * Testing if the shift get all does not return a null instance.
     * Test passed:1 √
     **/
    @Test
    void testGetAllShifts(){
        assertThat(this.shiftControleur.allShifts()).isNotNull();
    }


    /**
     * Testing the creation of a new shift with the given user at the given shop.
     * We test all the four possible cases!
     * number| user | shop |
     * ---------------------
     * 1-   | yes  | yes  | (Both user and shop exist)
     * 2-  | yes  | no   | (User exists, but shop doesn't exist)
     * 3-  | no   | yes  | (User doesn't exist, but shop exists)
     * 4-  | no   | no   | (Both user and shop don't exist)
     * Test passed:1 NOT YET....
     **/
    @Test
    void addUserToAShift() {
        // testing the four cases.
        testCase1();

        testCase2();

        testCase3();

        testCase4();

        /* Now that the core values of a shift are tested (User and shop).
         * let's test its values:
         * 1- Check if the new shift startTime is older than now
         *          >> isShiftOld(newShift);
         * 2- Check if the user has an ongoing shift at any shop
         *          >> doesShiftOverlap(user, newShift.getStartTime(), newShift.getEndTime());
         * 3- Check the duration is valid
         *          >> (shiftDuration > 8 || shiftDuration <= 0);
         * 4- check if the user has reached 8h/shop
         *          >> (hasWorked8hWithin24hInShop(newShift))
         * 5- Check if the user hasn't worked 5 days in a row at the same shop
         *          >> (hasWorkedFiveDaysInaRowInThisShop(newShift))
         * -----------------------------------------------------------------------
         * All good if all tests are passed.
         */
    }

    /**
     * 5- Check if the user hasn't worked 5 days in a row at the same shop
     * Test passed:1 √
     **/
    @Test
    void testHasWorked5DaysInARowAtShop() {
        User user = new User("alice@gmail.com", "Alice Laice");
        Shop shop = new Shop("Home shop");
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        // Define shift duration at 6 to avoid the condition 8h max /shop.
        long shiftDuration = 6; // Change this as needed

        Shift shift1 = new Shift(user, shop, startTime, shiftDuration);
        assertThat(this.shiftControleur.createNewShift(shift1).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Shift shift2 = new Shift(user, shop, startTime.plusDays(1), shiftDuration);
        assertThat(this.shiftControleur.createNewShift(shift2).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Shift shift3 = new Shift(user, shop, startTime.plusDays(2), shiftDuration);
        assertThat(this.shiftControleur.createNewShift(shift3).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Shift shift4 = new Shift(user, shop, startTime.plusDays(3), shiftDuration);
        assertThat(this.shiftControleur.createNewShift(shift4).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Shift shift5 = new Shift(user, shop, startTime.plusDays(4), shiftDuration);
        assertThat(this.shiftControleur.createNewShift(shift5).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Shift shift6 = new Shift(user, shop, startTime.plusDays(5), shiftDuration);
        Throwable exception1 = assertThrows(IllegalArgumentException.class, () -> this.shiftControleur.createNewShift(shift6));
        assertThat(exception1.getMessage()).isEqualTo("Shift creation denied! The User has worked 5 days in a row Hours at this shop");
    }


    /**
     * 4- checks if the user has reached 8h/shop
     * Test passed:1 √
     **/
    @Test
    void testHasWorked8hWithin24hInShop() {
        /*
         * Scenario:
         * user books a shift on the 20 december at 08:00 with a shiftDuration of 6h.
         * the shift1 endTime is at 20/december 14:00
         *
         * user books another shift2 on the same day starting 30 after the end of the first one.
         * the shift2 should return hasWorked8hPerShop error:
         *  */
        long duration1 = 6;
        LocalDateTime startTime1 = LocalDateTime.now().plusDays(1);
        User existingUser = new User("alice@gmail.com", "Alice Laice");
        Shop existingShop = new Shop("Home shop");

        // we create a shift in the specific date and time
        Shift shift1 = new Shift(existingUser, existingShop, startTime1, duration1);
        // here we assert the first shift is added without errors.
        assertThat(this.shiftControleur.createNewShift(shift1).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // we create a shift that does not overlap but still within the same day
        long duration2 = 3;
        LocalDateTime startTime2 = shift1.getEndTime().plusMinutes(10);

        Shift shift2 = new Shift(existingUser, existingShop, startTime2, duration2);

        // here we assert the first shift is added without errors.
        Throwable exception1 = assertThrows(IllegalArgumentException.class, () -> this.shiftControleur.createNewShift(shift2));
        assertThat(exception1.getMessage()).isEqualTo("Shift creation denied! The User: has worked 8 Hours at this shop within a 24 Hours window");
    }

    /**
     * 3- Checks the duration is valid:
     * case 1 : shiftDuration <= 0
     * case 2 : shiftDuration > 8
     * To prevent working in multiple shops simultaneously.
     * Test passed:1 √
     **/
    @Test
    void testIsShiftDurationValid() {
        //case 1
        long duration1 = -4;
        User existingUser = new User("alice@gmail.com", "Alice Laice");
        Shop existingShop = new Shop("Home shop");
        Shift shift1 = new Shift(existingUser, existingShop, LocalDateTime.now().plusDays(1), duration1);

        Throwable exception1 = assertThrows(IllegalArgumentException.class, () -> this.shiftControleur.createNewShift(shift1));
        assertThat(exception1.getMessage()).isEqualTo("Shift duration must be greater than 0 and <= 8h per shop!");

        // case 2
        long duration2 = 10;
        Shift shift2 = new Shift(existingUser, existingShop, LocalDateTime.now().plusDays(1), duration2);

        Throwable exception2 = assertThrows(IllegalArgumentException.class, () -> this.shiftControleur.createNewShift(shift2));
        assertThat(exception2.getMessage()).isEqualTo("Shift duration must be greater than 0 and <= 8h per shop!");

    }

    /**
     * 2- Checks if the user has an ongoing shift at any shop.
     * To prevent working in multiple shops simultaneously.
     * Test passed:1 √
     **/
    @Test
    void testDoesShiftOverlap() {
        // using only four hours to avoid triggering the hasWorked8hWithin24hInShop exception
        long duration = 4;
        User existingUser = new User("alice@gmail.com", "Alice Laice");
        Shop existingShop = new Shop("Home shop");

        // I use plus one day because:
        // I believe the tests keep memory of vars. So when I use LocalDateTime.now()
        // I always get isShiftOld triggered.
        Shift shift1 = new Shift(existingUser, existingShop, LocalDateTime.now().plusDays(1), duration);
        // here we assert the first shift is added without errors.
        assertThat(this.shiftControleur.createNewShift(shift1).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // here we create a new shift that overlaps the shift1 by one hour >> plusHours(1)

        Shift shift2 = new Shift(existingUser, existingShop, LocalDateTime.now().plusDays(1).plusHours(1), duration);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> this.shiftControleur.createNewShift(shift2));
        assertThat(exception.getMessage()).isEqualTo("Cannot book a new shift. You have an ongoing shift already!");

    }

    /**
     * 1- Checks if the new shift startTime is older than now >> isShiftOld(newShift);
     * Test passed:1 √
     **/
    @Test
    void testIsShiftOld() {
        long duration = 8;
        User existingUser = new User("alice@gmail.com", "Alice Laice");
        Shop existingShop = new Shop("Home shop");

        Shift shift = new Shift(existingUser, existingShop, LocalDateTime.now().minusMonths(1), duration);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> this.shiftControleur.createNewShift(shift));
        assertThat(exception.getMessage()).isEqualTo("Denied! The shift you selected is in the past...");

    }

    /**
     * Case 4
     * I believe it is a redundant test, because:
     * In the ShiftService, we first check for the user if null.
     * Hence, the first exception that will trigger is the illegalArgument with the message:
     * <"No such User! Canceling request...">
     * Nevertheless, I include it :)
     * | user | Shop |
     * | no  | no   |
     * Test passed:1 √
     **/
    private void testCase4() {
        long duration = 8;
        User nonExistingUser = new User("aliceInWonderland@gmail.com", "Alice Laice");
        Shop existingShop = new Shop("Homes shop");

        Shift shift2 = new Shift(nonExistingUser, existingShop, LocalDateTime.now().plusDays(1), duration);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> this.shiftControleur.createNewShift(shift2));
        assertThat(exception.getMessage()).isEqualTo("No such User! Canceling request...");


    }

    /**
     * Case 3
     * | user | Shop |
     * | no  | yes   |
     * Test passed:1 √
     **/
    private void testCase3() {
        long duration = 8;
        User nonExistingUser = new User("aliceInWonderland@gmail.com", "Alice Laice");
        Shop existingShop = new Shop("Home shop");

        Shift shift2 = new Shift(nonExistingUser, existingShop, LocalDateTime.now().plusDays(1), duration);
        // in the shift service, we always check for the user if null.
        // Hence, the first exception that will trigger is the illegalArgument with the message:
        // <"No such User! Canceling request...">
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> this.shiftControleur.createNewShift(shift2));
        assertThat(exception.getMessage()).isEqualTo("No such User! Canceling request...");


    }

    /**
     * Case 2
     * | user | Shop |
     * | yes  | no   |
     * Test passed:1 √
     **/
    private void testCase2() {
        long duration = 8;
        User existingUser = new User("alice@gmail.com", "Alice Laice");
        Shop nonExistingShop = new Shop("Homes shop");

        Shift shift2 = new Shift(existingUser, nonExistingShop, LocalDateTime.now().plusDays(1), duration);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> this.shiftControleur.createNewShift(shift2));
        assertThat(exception.getMessage()).isEqualTo("No such Shop! Canceling request...");


    }

    /**
     * Case 1
     * | user | Shop |
     * | yes  | yes   |
     * User exists because it is saved auto in UserConfig
     * Shop exists because it is saved auto in ShopConfig
     * ShiftDuration 8hours -> 0 < duration <= 8
     * Test passed:1 √
     **/
    private void testCase1() {
        long duration = 8;
        User existingUser = new User("alice@gmail.com", "Alice Laice");
        Shop exisitingShop = new Shop("Home shop");
        // case 1 existing user and shop
        Shift shift1 = new Shift(existingUser, exisitingShop, LocalDateTime.now().plusDays(1), duration);
        assertThat(this.shiftControleur.createNewShift(shift1).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }


}
