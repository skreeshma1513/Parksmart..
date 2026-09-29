package com.example.parksmart.service;

import com.example.parksmart.entity.Booking;
import com.example.parksmart.entity.CheckInOut;
import com.example.parksmart.entity.Slot;
import com.example.parksmart.repository.BookingRepository;
import com.example.parksmart.repository.CheckInOutRepository;
import com.example.parksmart.repository.SlotRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInOutService {

    private static final double PENALTY_PER_MINUTE = 1.0;

    @Autowired
    private CheckInOutRepository repository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SlotRepository slotRepository;

    public CheckInOut create(CheckInOut checkInOut) {

        Booking booking = bookingRepository
                .findById(checkInOut.getBooking().getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check-in must be within booking time
        if (checkInOut.getCheckInTime()
                .isBefore(booking.getStartTime())) {

            throw new RuntimeException(
                    "Check-in cannot be before booking start time");
        }

        // Calculate overstay penalty
        if (checkInOut.getCheckOutTime() != null) {

            LocalDateTime bookedEndTime = booking.getEndTime();
            LocalDateTime actualCheckOut = checkInOut.getCheckOutTime();

            if (actualCheckOut.isAfter(bookedEndTime)) {

                long extraMinutes = Duration.between(
                        bookedEndTime,
                        actualCheckOut).toMinutes();

                double penalty = extraMinutes * PENALTY_PER_MINUTE;

                checkInOut.setPenalty(penalty);

            } else {

                checkInOut.setPenalty(0);
            }

            // Make slot available after checkout
            Slot slot = booking.getSlot();

            if (slot != null) {
                slot.setStatus("AVAILABLE");
                slotRepository.save(slot);
            }
        }

        checkInOut.setBooking(booking);

        return repository.save(checkInOut);
    }

    public List<CheckInOut> getAll() {
        return repository.findAll();
    }

    public CheckInOut getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Check-in/Check-out record not found"));
    }
}