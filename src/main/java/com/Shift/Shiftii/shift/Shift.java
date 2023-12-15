package com.Shift.Shiftii.shift;

import com.Shift.Shiftii.shops.Shop;
import com.Shift.Shiftii.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "Shifts")
public final class Shift {

    @Id
    @SequenceGenerator(
            name = "shift_id_sequence",
            sequenceName = "shift_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "shift_id_sequence"
    )

    @Column(nullable = false, unique = true, updatable = false)
    private Long shiftId;

    private LocalDateTime startTime;
    private long shiftDuration;
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("shifts")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, targetEntity = Shop.class)
    @JsonBackReference
    @JsonIgnoreProperties("shifts")
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public Shift() {
    }

    //  As Zero Trust security concept:
    //  The endTime should be automatically updated based on the shift duration and the start-Time.
    public Shift(User user, Shop shop, LocalDateTime startTime, long shiftDuration) {
        this.user = user;
        this.shop = shop;
        this.startTime = startTime;
        this.shiftDuration = shiftDuration;
        setEndTime(); // set it in constructor with a private setter.
    }


    public long getShiftDuration() {
        return this.shiftDuration;
    }

    public User getUser() {
        return this.user;
    }

    public Long getShiftId() {
        return this.shiftId;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    /* I learnt that transient will make the endTime not added to db!
     so we need to configure it here: */
    public LocalDateTime getEndTime() {
        return this.endTime;
    }


    public Shop getShop() {
        return this.shop;
    }


    public void setShiftDuration(long shiftDuration) {
        this.shiftDuration = shiftDuration;
    }

    private void setEndTime() {
        this.endTime = this.startTime.plusHours(this.shiftDuration);
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return Objects.equals(this.shiftId, shift.shiftId) &&
                Objects.equals(this.startTime, shift.startTime) &&
                Objects.equals(this.endTime, shift.endTime) &&
                Objects.equals(this.user, shift.user) &&
                Objects.equals(this.shop, shift.shop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.shiftId, this.startTime, this.endTime, this.user, this.shop);
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftId=" + this.shiftId +
                ", startTime=" + this.startTime +
                ", shiftDuration=" + this.shiftDuration +
                ", endTime=" + this.endTime +
                ", user=" + this.user +
                ", shop=" + this.shop +
                '}';
    }
}
