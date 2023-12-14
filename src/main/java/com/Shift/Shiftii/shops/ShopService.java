package com.Shift.Shiftii.shops;

import com.Shift.Shiftii.shift.Shift;
import com.Shift.Shiftii.shift.ShiftRepository;
import com.Shift.Shiftii.user.User;
import com.Shift.Shiftii.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    //todo remove userRepo from here /!\

    @Autowired
    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    // TODO: 10/12/2023 replace modifiers with Optional instead. and use Optional.get in the controleur

    public Shop getShopById(Long id) {
        if (this.shopRepository.findShopsByShop_id(id).isPresent())
            return this.shopRepository.findShopsByShop_id(id).get();
        return null;
    }

    /**
     * Retrieves a list of all the available shops.
     *
     * @return A list of shops if available; otherwise, an empty list.
     */
    public List<Shop> getShopsList() {
        return this.shopRepository.allShops().orElse(Collections.emptyList());
    }

    /**
     * Checks if a shop exists by the provided name.
     *
     * @param name The name of the shop to check.
     * @return true if the shop with the given name exists; otherwise, false.
     */
    public boolean shopExistsByName(String name) {
        return this.shopRepository.existsByShopName(name);
    }

    /**
     * Retrieves a shop by its name.
     *
     * @param shopName The name of the shop to retrieve.
     * @return The corresponding shop by the provided name,or null if not found.
     */
    public Shop requestedShopByName(String shopName) {
        return this.shopRepository.findShopsByShopName(shopName).orElse(null);
    }

    /**
     * Adds a new shop to the repository if it doesn't already exist.
     *
     * @param shop The shop to be added.
     * @return {@code true} if the shop is successfully added, {@code false} otherwise.
     * @throws DuplicateKeyException if a shop with the same name already exists.
     */
    public boolean addNewShop(Shop shop) {
        // TODO: 08/12/2023
        final Optional<Shop> shopQuery = this.shopRepository.findShopsByShopName(shop.getShopName());
        if (shopQuery.isPresent()) {
            throw new DuplicateKeyException("Shop already exists!");
        } else {
            this.shopRepository.save(shop);
            return true;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopService that = (ShopService) o;
        return Objects.equals(this.shopRepository, that.shopRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.shopRepository);
    }

    @Override
    public String toString() {
        return this.shopRepository.getClass().getName();
    }
}
