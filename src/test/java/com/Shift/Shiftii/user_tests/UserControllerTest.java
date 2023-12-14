package com.Shift.Shiftii.user_tests;

import com.Shift.Shiftii.user.User;
import com.Shift.Shiftii.user.UserControleur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserControleur userControleur;

    /**
     * General test to ensure a proper context loading of the user controller
     * Test passed:1 √
     **/
    @Test
    void contextLoads() {
        assertThat(this.userControleur).isNotNull();
    }


    /**
     * Testing if get all users does not return a null instance.
     * Test passed:1 √
     **/
    @Test
    void testGetAllUsers() {
        assertThat(this.userControleur.users()).isNotNull();
    }


    /**
     * Test case for finding a user by email address
     * Test passed:1 √
     **/
    @Test
    void testGetUserByEmail() {
        final String email = "alice@gmail.com"; // an existing user's email
        assertThat(this.userControleur.userByEmail(email).getStatusCode()).isEqualTo(HttpStatus.OK);
        final String nonExistingEmail = "aliceWonderland@gmail.com"; // a non-existing user's email
        assertThat(this.userControleur.userByEmail(nonExistingEmail).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Test case for creating users with the same identifier: email address
     * Test passed:1 √
     **/
    @Test
    public void testCreatingNewUsers() {

        // creating a non-existing user, is expected to return <201 CREATED>.
        User user1 = new User("sofie@gmail.com", "Sofie Foise");
        assertThat(this.userControleur.createNewUser(user1).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        User user2 = new User("sofie@gmail.com", "Trine Trein");
        // Attempt to create a second user with the same email >> Denied

        Throwable exception = assertThrows(DuplicateKeyException.class, () -> this.userControleur.createNewUser(user2));
        assertThat(exception.getMessage()).isEqualTo("User already exists!");

    }
}
