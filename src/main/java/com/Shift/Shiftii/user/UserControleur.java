package com.Shift.Shiftii.user;

import com.Shift.Shiftii.shift.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/users")
public final class UserControleur {
    private final UserService userService;


    @Autowired
    public UserControleur(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> users() {
        return this.userService.getUzersList();
    }


    @GetMapping("/id")
    public ResponseEntity<User> userByEmail(String userEmail) {
        if (this.userService.userByMail(userEmail) != null)
            return new ResponseEntity<>(this.userService.userByMail(userEmail),HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Void> createNewUser(@Valid @RequestBody User user) {
        if (this.userService.addNewUzer(user))
            return new ResponseEntity<>(HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}




