package com.Shift.Shiftii.shops;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/shops")
public class ShopControleur {
    private final ShopService shopService;

    @Autowired
    public ShopControleur(ShopService shopService) {
        this.shopService = shopService;
    }


    @GetMapping("/id/{shop_id}")
    public ResponseEntity<Shop> getShopByID(@Valid @PathVariable Long shop_id) {
        final Shop requestedShop = this.shopService.getShopById(shop_id);
        if (requestedShop != null)
            return new ResponseEntity<>(requestedShop, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/name/{shopName}")
    public ResponseEntity<Shop> getShopByName(@Valid @PathVariable String shopName) {
        if (this.shopService.shopExistsByName(shopName))
            return new ResponseEntity<>(this.shopService.requestedShopByName(shopName), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping
    public ResponseEntity<List<Shop>> shopsList() {
        if (!this.shopService.getShopsList().isEmpty())
            return new ResponseEntity<>(this.shopService.getShopsList(), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<Void> createNewShop(@Valid @RequestBody Shop shop) {
        if (this.shopService.addNewShop(shop))
            return new ResponseEntity<>(HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
