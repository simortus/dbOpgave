package com.Shift.Shiftii.shops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {


    /**
     * 1. user is able to retrieve a shop by name or id
     * 2. user can list all available shops.
     * 3. User should not create shop! not secure for the system.
     * **/


    boolean existsByShopName(String shopName);
    @Query("select shp from Shop shp")
    Optional<List<Shop>> allShops();

    @Query("select shp from Shop shp where shp.shopName= ?1")
    Optional<Shop> findShopsByShopName(String shopName);

    @Query("select shp from Shop shp where shp.shop_id= ?1")
    Optional<Shop> findShopsByShop_id(@NonNull Long shopId);





}
