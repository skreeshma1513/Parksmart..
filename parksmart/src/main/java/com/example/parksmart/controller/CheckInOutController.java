package com.example.parksmart.controller;

import com.example.parksmart.entity.CheckInOut;
import com.example.parksmart.entity.Booking;
import com.example.parksmart.repository.BookingRepository;
import com.example.parksmart.service.CheckInOutService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/check-in-out")
public class CheckInOutController {

    @Autowired
    private CheckInOutService service;

    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CheckInOut create(
            @Valid @RequestBody CheckInOut checkInOut) {

        if (checkInOut.getBooking() == null ||
                checkInOut.getBooking().getId() == null) {

            throw new RuntimeException(
                    "Please enter a valid Booking ID");
        }

        Booking booking = bookingRepository
                .findById(checkInOut.getBooking().getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        checkInOut.setBooking(booking);

        return service.create(checkInOut);
    }

    @GetMapping
    public List<CheckInOut> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CheckInOut getById(@PathVariable Long id) {
        return service.getById(id);
    }
}