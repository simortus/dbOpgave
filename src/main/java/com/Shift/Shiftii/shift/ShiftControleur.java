package com.Shift.Shiftii.shift;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/shifts")
public class ShiftControleur {

    @Autowired
    private final ShiftService shiftService;

    public ShiftControleur(ShiftService shiftService) {
        this.shiftService = shiftService;
    }


    @GetMapping("/all")
    public List<Shift> allShifts() {
        return this.shiftService.getAllShifts();
    }

    @PostMapping("/assigned shifts")
    public ResponseEntity<Void> createNewShift(@Valid @RequestBody Shift shift) {
        if (this.shiftService.addNewShiftToShop(shift))
            return new ResponseEntity<>(HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
