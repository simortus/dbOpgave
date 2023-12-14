package com.Shift.Shiftii.shops;

import com.Shift.Shiftii.shift.Shift;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Rule: A user can work in different shops
 * Relation: @ManyToMany with Shifts
 **/
@Entity
@Table(name = "Shops")

public class Shop {

    @Id
    @SequenceGenerator(
            name = "shop_id_sequence",
            sequenceName = "shop_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "shop_id_sequence"
    )

    @Column(nullable = false, unique = true, updatable = false)
    private Long shop_id;
    @Column(nullable = false, unique = true, updatable = false)
    private String shopName;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, orphanRemoval = true,targetEntity = Shift.class)
    @JsonIgnoreProperties("shifts")
    private List<Shift> shifts;

    public Shop() {
    }

    public Shop(String shopName, List<Shift> shifts) {
        this.shopName = shopName;
        this.shifts = shifts;
    }

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public List<Shift> getShifts() {
        return this.shifts;
    }

    public Long getShop_id() {
        return this.shop_id;
    }

    public String getShopName() {
        return this.shopName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop shop = (Shop) o;
        return Objects.equals(this.shop_id, shop.shop_id) && Objects.equals(this.shopName, shop.shopName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shop_id, shopName);
    }

    @Override
    public String toString() {
        return "Shops{" +
                "id=" + this.shop_id +
                ", shopName='" + this.shopName + '\'' +
                '}';
    }


}
