package com.Shift.Shiftii.user;

import com.Shift.Shiftii.shift.Shift;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Objects;


/**
 * Rule: A user can work in different shops
 * Relation: @OneToMany with Shifts
 **/
@Entity
@Table(name = "Users")
public final class User {

    @Id
    @SequenceGenerator(
            name = "user_id_sequence",
            sequenceName = "user_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "user_id_sequence"
    )
    private Long userId;
    @Email(message = "Invalid email address!")
    //The @Email annotation in Spring already includes a built-in validation for email addresses based on the RFC 5322 standard!
    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[a-zA-Z_ -]+$", message = "Invalid name format")
    // regexPatter to match a name that only contains (a-z_ or (A-Z), space and dash & underscore.
    private String name; // can be updated ?!

    //user can have multiple ships not overlapping!


    @OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, orphanRemoval = true, targetEntity = Shift.class)
    @JsonIgnoreProperties("shifts")
    private List<Shift> shifts;



    public User() {
    }

    public User(String email, String name, List<Shift> shifts) {
        this.email = email;
        this.name = name;
        this.shifts = shifts;
    }

    public User(Long userId) {
        this.userId = userId;
    }


    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }


    public List<Shift> getShifts() {
        return this.shifts;
    }

    /**
     * We use the email to identify the user.
     * Hence, we need a constructor with only this value to identify the user on shift creation
     */
    public User(String email) {
        this.email = email;
    }

    public User(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }


    public Long getUserId() {
        return this.userId;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(this.userId, user.userId) &&
                Objects.equals(this.email, user.email) &&
                Objects.equals(this.name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.email, this.name, this.shifts);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + this.userId +
                ", email='" + this.email + '\'' +
                ", name='" + this.name + '\'' +
                '}';
    }
}

