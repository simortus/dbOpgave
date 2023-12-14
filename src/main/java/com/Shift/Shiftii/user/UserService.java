package com.Shift.Shiftii.user;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public final class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a list of all users stored in the repository.
     *
     * @return A list containing all users, empty list if no users are found.
     */
    public List<User> getUzersList() {
        return this.userRepository.getAllUsers().orElse(Collections.emptyList());
    }

    /**
     * Checks whether a user with the given email exists.
     *
     * @param email The email address of the user to check.
     * @return True if a user with the provided email exists, otherwise false.
     */
    public User userByMail(String email) {
        return this.userRepository.findUserByEmails(email).orElse(null);
    }


    /**
     * Adds a new user to the system if the user with the provided email doesn't already exist.
     *
     * @param user The user to be added.
     * @return True if the user is successfully added, otherwise throws a DuplicateKeyException.
     * @throws DuplicateKeyException If a user with the same email already exists in the system.
     */
    public boolean addNewUzer(User user) {
        //  Since ID is generated auto, we use the email from the request body to check if the user exists
        if (this.userRepository.existsByEmail(user.getEmail()))
            throw new DuplicateKeyException("User already exists!");
        this.userRepository.save(user);
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserService that = (UserService) o;
        return Objects.equals(this.userRepository, that.userRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userRepository);
    }

    @Override
    public String toString() {
        return this.userRepository.getClass().getName();
    }
}
