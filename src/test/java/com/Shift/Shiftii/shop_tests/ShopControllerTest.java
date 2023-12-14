package com.Shift.Shiftii.shop_tests;

import com.Shift.Shiftii.shops.Shop;
import com.Shift.Shiftii.shops.ShopControleur;
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
public class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ShopControleur shopControleur;

    /**
     * General test to ensure a proper context loading of the shop controller
     * Test passed:1 √
     **/
    @Test
    void contextLoads() {
        assertThat(this.shopControleur).isNotNull();
    }

    /**
     * Testing if get all shops does not return a null instance.
     * Test passed:1 √
     **/
    @Test
    void testGetAllUsers() {
        assertThat(this.shopControleur.shopsList()).isNotNull();
    }

    /**
     * Testing if getting a shop by name return 200OK if exists, else 404 NOT_FOUND
     * Test passed:1 √
     **/
    @Test
    void testGetShopByID() {

        // should return exists (200 OK HTTP code).
        assertThat(this.shopControleur.getShopByID(1L).getStatusCode()).isEqualTo(HttpStatus.OK);

        // no shop with ID 200L should return 404 not found !
        assertThat(this.shopControleur.getShopByID(200L).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Testing if getting a shop by name return 200OK if exists, else 404 NOT_FOUND
     * Test passed:1 √
     **/
    @Test
    void testGetShopByName() {
        // should return exists (200 OK HTTP code).
        assertThat(this.shopControleur.getShopByName("Home shop").getStatusCode()).isEqualTo(HttpStatus.OK);

        // no shop with such name should return 404 not found !
        assertThat(this.shopControleur.getShopByName("Cinema shop").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Test case for creating shops with the same identifier: shop name
     * Test passed:1 √
     **/
    @Test
    void testCreatingNewShop() {
        // creating a non-existing user, is expected to return <201 CREATED>.
        Shop shop1 = new Shop("Antique shop");
        assertThat(this.shopControleur.createNewShop(shop1).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Shop shop2 = new Shop("Antique shop");
        // Attempt to create a second user with the same email
        Throwable exception = assertThrows(DuplicateKeyException.class, () -> this.shopControleur.createNewShop(shop2));
        // Optional: Asserting we get the expected message for the performance sake and having user-friendly responses.
        assertThat(exception.getMessage()).isEqualTo("Shop already exists!");
    }
}
