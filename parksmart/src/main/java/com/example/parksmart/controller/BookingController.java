package com.example.parksmart.controller;

import com.example.parksmart.entity.Booking;
import com.example.parksmart.entity.Slot;
import com.example.parksmart.repository.SlotRepository;
import com.example.parksmart.service.BookingService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService service;

    @Autowired
    private SlotRepository slotRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@Valid @RequestBody Booking booking) {

        /*
         * Make sure the selected slot is loaded
         * from the database before sending it
         * to the service layer.
         */

        if (booking.getSlot() == null ||
                booking.getSlot().getId() == null) {

            throw new RuntimeException(
                    "Please select a valid parking slot");
        }

        Long slotId = booking.getSlot().getId();

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException(
                        "Parking slot not found"));

        booking.setSlot(slot);

        return service.create(booking);
    }

    @GetMapping
    public List<Booking> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Booking getById(
            @PathVariable Long id) {

        return service.getById(id);
    }
}