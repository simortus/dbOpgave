package com.Shift.Shiftii.shift;

import com.Shift.Shiftii.shops.Shop;
import com.Shift.Shiftii.user.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {


    // if user has an ongoing shift.
    /* Cool fact I learnt>> boolean existsByUserAndEndTimeAfter(User user, LocalDateTime now);
    in a Spring Data JPA repository, Spring Data JPA creates the query automatically
    based on the method name. This is thanks to Spring Data JPA's method naming conventions.*/
    boolean existsByUserAndEndTimeAfter(User user, LocalDateTime now);

    @Query("select  sh from Shift sh")
    Optional<List<Shift>> getAll();

    //select s from Shifts  where shift_id= ?1
    @Query("select sh from Shift sh where sh.user.email=?1")
    Optional<List<Shift>> findShiftsByUserEmail(String email);

    @Query("select sh from Shift sh where sh.shiftId=?1")
    Optional<Shift> findShiftsById(Long shiftId);
    


    @Query(value = "SELECT sh FROM Shift sh " +
            "WHERE sh.user.email = :email " +
            "AND sh.shop.shopName = :shopName " +
            "AND sh.startTime >= :yesterday  ")
    Optional<List<Shift>> findShiftsInShopForUserLast24Hours(@Param("email") String email,
                                                             @Param("shopName") String shopName,
                                                             @Param("yesterday") LocalDateTime yesterday);

    @Query("SELECT sh FROM Shift sh WHERE sh.user.email = :email AND sh.shop.shopName = :shopName")
    Optional<List<Shift>> findShiftsInShopForUser(@Param("email") String userEmail, @Param("shopName") String shopName);

    @Query(value = "SELECT sh FROM Shift sh " +
            "WHERE sh.user.email = :email " +
            "AND sh.shop.shopName = :shopName " +
            "AND sh.startTime >= :fiveDaysAgo  ")
    Optional<List<Shift>> findShiftsInShopForUserWithin5Days(@Param("email") String userEmail,
                                                             @Param("shopName") String shopName,
                                                             @Param("fiveDaysAgo") LocalDateTime fiveDaysAgo);
}

/*
  side notes:
      //The =?1 (PlaceHolder for an argument :))
      //The = :value >> named parameters >> better readability when multiple params!
 */


