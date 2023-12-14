package com.Shift.Shiftii.shift;

import com.Shift.Shiftii.shops.Shop;
import com.Shift.Shiftii.shops.ShopRepository;
import com.Shift.Shiftii.user.User;
import com.Shift.Shiftii.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public final class ShiftService {

//    private static final long MAX_HOURS_PER_DAY = 10; // thought of it, but no time to bother for now±!

    private static final long MAX_HOURS_AT_SHOP_WITHIN_24_HOURS = 8;
    public static final long MAX_DAYS_IN_A_ROW_AT_SHOP = 5;

    private final ShiftRepository shiftRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository, ShopRepository shopRepository, UserRepository userRepository) {
        this.shiftRepository = shiftRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }


    public List<Shift> getAllShifts() {
        return this.shiftRepository.getAll().orElse(Collections.emptyList());
    }

    /**
     * Calculates the total duration of the user's shifts within a 24-hour window.
     *
     * @param shifts list of the user's shifts to calculate the total duration
     * @return the sum of shift durations within the last 24 hours
     */
    private long shiftDurationTotalCalculator(List<Shift> shifts) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusHours(24);

        return shifts.stream()
                .filter(shift -> shift.getStartTime().isAfter(yesterday) && shift.getEndTime().isBefore(now))
                .mapToLong(Shift::getShiftDuration)
                .sum();

    }


    /**
     * Retrieves all the ongoing shifts for a given user based on their email.
     * Filters shifts by end time greater than the current time.
     *
     * @param user for whom we retrieved ongoing shifts.
     * @return a list of ongoing shifts for this user
     */
    public List<Shift> ongoingShiftsForUser(User user) {
        final LocalDateTime now = LocalDateTime.now();
        Optional<List<Shift>> optionalShiftList = this.shiftRepository.findShiftsByUserEmail(user.getEmail());
        if (optionalShiftList.isEmpty())
            return Collections.emptyList();
        List<Shift> userShiftList = optionalShiftList.get();
        return userShiftList.stream()
                .filter(shift -> shift.getEndTime().isAfter(now))
                .collect(Collectors.toList());
    }

    /**
     * Checks whether a new shift overlaps with any ongoing shifts for the current user.
     *
     * @param currentUser        the user for whom to check the shift overlap
     * @param newShiftStartTime the start time of the new shift
     * @param newShiftEndTime   the end time of the new shift
     * @return true if there's an overlap, false otherwise
     */
    private boolean doesShiftOverlap(User currentUser, LocalDateTime newShiftStartTime, LocalDateTime newShiftEndTime) {
        List<Shift> userShifts = ongoingShiftsForUser(currentUser);
        if (userShifts.isEmpty())
            return false;
        for (Shift shift : userShifts) {
            final LocalDateTime existingShiftStartTime = shift.getStartTime();
            final LocalDateTime existingShiftEndTime = shift.getEndTime();

            // Check for overlap condition: If the new shift starts before the existing shift ends
            // AND the new shift ends after the existing shift starts, it's an overlap
            if (newShiftStartTime.isBefore(existingShiftEndTime) && newShiftEndTime.isAfter(existingShiftStartTime)) {
                return true; // There is an overlap
            }
        }
        return false; // No overlap found
    }

    /**
     * Retrieves the shop entity based on the provided shop name.
     *
     * @param shopName the name of the shop to retrieve
     * @return the Shop entity corresponding to the shop name, or null if not found
     */
    private Shop requestedShop(String shopName) {
        return this.shopRepository.findShopsByShopName(shopName).orElse(null);
    }

    /**
     * Retrieves the user entity based on the provided email.
     *
     * @param userEmail the email of the user to retrieve shifts for.
     * @return the User entity corresponding to the email, or null if not found
     */
    private User requestedUser(String userEmail) {
        //could add if exists before this
        return this.userRepository.findUserByEmails(userEmail).orElse(null);
    }

    /**
     * Retrieves a list of shifts for a user based on the email in the named shop
     * within the last 24 hours (now-minus 24hours).
     *
     * @param userEmail                 the user's email for whom to get the shifts.
     * @param shopName                  the shop's name where to look for the user's shift.
     * @param newShiftStartTimeMinus24H the start time minus 24 hours to filter shifts
     * @return a list of shifts for the requested user in the specified shop within
     * the last 24 hours if any are found,or else an empty list!
     */
    private List<Shift> shiftsInThisSHopForThisUserIn24H(String userEmail, String shopName, LocalDateTime newShiftStartTimeMinus24H) {
        return this.shiftRepository.
                findShiftsInShopForUserLast24Hours(
                        userEmail,
                        shopName,
                        newShiftStartTimeMinus24H).
                orElse(Collections.emptyList());
    }

    /**
     * Get the user's shift list in the specified shop.
     *
     * @param userEmail the email of the user to retrieve shifts for.
     * @param shopName  the name of the shop to look in.
     * @return a list of shifts for the requested user in the specified shop within, or else an empty list!
     */
    private List<Shift> shiftsInThisShopForThisUser(String userEmail, String shopName) {
        return this.shiftRepository.
                findShiftsInShopForUser(
                        userEmail,
                        shopName).
                orElse(Collections.emptyList());
    }

    /**
     * Checks if a user has worked > 8 hours within the last 24 hours at the given shop.
     *
     * @param newShift the shift to be evaluated for the user:
     *
     * @return true if the user has not worked more than 8 hours in the last 24 hours, false otherwise
     */
    private boolean hasWorked8hWithin24hInShop(Shift newShift) {
        // TODO: 12/12/2023
        // Get all shifts of the user in a 24h period based on the shift startTime.
        final List<Shift> userShifts = shiftsInThisSHopForThisUserIn24H(
                newShift.getUser().getEmail(),
                newShift.getShop().getShopName(),
                newShift.getStartTime().minusDays(1));
        if (userShifts.isEmpty())
            return false;
        // Calculate total duration within 24 hours
        final long totalDurationWithin24Hours = shiftDurationTotalCalculator(userShifts);
        // Check if total duration exceeds 8 hours (add the new shift duration as well as it is within the 24h window
        System.out.println("Total hours in this shop: " + totalDurationWithin24Hours);
        return totalDurationWithin24Hours < MAX_HOURS_AT_SHOP_WITHIN_24_HOURS + newShift.getShiftDuration();
    }

    /**
     * 1- Get all shifts for this user in this shop.
     * 2- Sort the shifts using Shift-startTime.
     * 3- Loop through the sorted shiftsList to find any consecutive shifts within 5days window.
     *
     * @param shift the new shift from the POST-requestBody
     * @return boolean true if fiveConsecutiveDays ==5, else false.
     **/
    private boolean hasWorkedFiveDaysInaRowInThisShop(Shift shift) {
        final List<Shift> userShiftsInSameShop = shiftsInThisShopForThisUser(
                shift.getUser().getEmail(),
                shift.getShop().getShopName()
        );
        userShiftsInSameShop.sort(Comparator.comparing(Shift::getStartTime));
        // if empty, no bother.
        if (userShiftsInSameShop.isEmpty())
            return false;

        // consecutive days counter (no 0day lol)
        return getConsecutiveDaysAtShop(userShiftsInSameShop) == MAX_DAYS_IN_A_ROW_AT_SHOP;

    }
    /**
     * Calculates the number of consecutive days a user has worked at the same shop based on the provided shift list..
     *
     * @param userShiftsInSameShop a list of shifts of the user in the same shop
     * @return the count of consecutive days the user has worked at the same shop
     */
    private int getConsecutiveDaysAtShop(List<Shift> userShiftsInSameShop) {
        int consecutiveDaysAtShop = 1;
        for (int i = 0; i < (userShiftsInSameShop.size() - 1); i++) {
            Shift currentShift = userShiftsInSameShop.get(i);
            Shift nextShift = userShiftsInSameShop.get(i + 1);
            LocalDateTime currentEndTime = currentShift.getEndTime();
            LocalDateTime nextStartTime = nextShift.getStartTime();
            //increment if true
            if (currentEndTime.plusDays(1).isAfter(nextStartTime))
                consecutiveDaysAtShop++;
        }
        return consecutiveDaysAtShop;
    }
    /**
     * Checks if the provided shift's start time is in the past (before current time).
     *
     * @param newShift the shift to be checked
     * @return true if the shift's start time is before the current time, false otherwise
     */
    private boolean isShiftOld(Shift newShift) {
        return newShift.getStartTime().isBefore(LocalDateTime.now());
    }

    /**
     * Adds a new shift for a user at a shop given the provided shift details.
     *
     * @param newShift The new shift to be added.
     * @return True if the shift is successfully added, otherwise throws IllegalArgumentException.
     * @throws IllegalArgumentException If the user or shop doesn't exist, if the shift is in the past,
     *                                  if the user has an ongoing shift, if the shift duration is invalid,
     *                                  if the user has worked 8 hours at the shop within 24 hours,
     *                                  or if the user has worked 5 days in a row at the shop.
     */
    public boolean addNewShiftToShop(Shift newShift) {
        // the values needed to inspect.
        final long shiftDuration = newShift.getShiftDuration();
        final String shopName = newShift.getShop().getShopName();
        final String userMail = newShift.getUser().getEmail();
        final LocalDateTime startTime = newShift.getStartTime();

        // Fetch Shop and User from the database based on the received request body
        final User user = requestedUser(userMail);
        final Shop shop = requestedShop(shopName);

        if (user == null)
            throw new IllegalArgumentException("No such User! Canceling request...");
        if (shop == null)
            throw new IllegalArgumentException("No such Shop! Canceling request...");

        /*
         * Check if the user's shift startTime is from now and ahead. Any shift that is old should be rejected.
         */
        if (isShiftOld(newShift))
            throw new IllegalArgumentException("Denied! The shift you selected is in the past...");

        /*
         *check if user has ongoing shift anywhere
         * If any shift overlaps with new shift:
         */
        System.out.println("Shift: " + newShift);
        if (doesShiftOverlap(user, newShift.getStartTime(), newShift.getEndTime()))
            throw new IllegalArgumentException("Cannot book a new shift. You have an ongoing shift already!");

        /*
         * If new shift's user has worked other shifts within 24H in the same shop.
         * Tackle the 8H limit/shop within 24h window.
         * 1- check for duration validity. If any user tries to put more than 8 Hours or >=0
         * */
        if (shiftDuration > 8 || shiftDuration <= 0)
            throw new IllegalArgumentException("Shift duration must be greater than 0 and <= 8h per shop!");
        // if duration is ok. check the user's history within this shop.
        if (hasWorked8hWithin24hInShop(newShift))
            throw new IllegalArgumentException("Shift creation denied! The User: has worked 8 Hours at this shop within a 24 Hours window");

        if (hasWorkedFiveDaysInaRowInThisShop(newShift))
            throw new IllegalArgumentException("Shift creation denied! The User has worked 5 days in a row Hours at this shop");

        System.out.println("User: " + user);
        System.out.println("Shop: " + shop);
        final Shift shift = new Shift(user, shop, startTime, shiftDuration);

        // Save the newShift entity
        this.shiftRepository.save(shift);
        return true;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftService that = (ShiftService) o;
        return Objects.equals(this.shiftRepository, that.shiftRepository)
                && Objects.equals(this.shopRepository, that.shopRepository)
                && Objects.equals(this.userRepository, that.userRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.shiftRepository, this.shopRepository, this.userRepository);
    }


    @Override
    public String toString() {
        return this.shiftRepository.getClass().getName();
    }
}
