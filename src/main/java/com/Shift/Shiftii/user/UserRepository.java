package com.Shift.Shiftii.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {


    //better and less code!
    boolean existsByEmail(String  email);


    @Query("select u from User u")
    Optional<List<User>> getAllUsers();

    @Query("select u from User u where u.email= ?1")
    Optional<User> findUserByEmails(String email);


    @Query("select u from User u where u.userId= ?1")
    Optional<User> findUserByUser_id(@NonNull Long userID);


}
