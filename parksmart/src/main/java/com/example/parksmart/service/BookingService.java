package com.example.parksmart.service;

import com.example.parksmart.entity.Booking;
import com.example.parksmart.entity.Slot;
import com.example.parksmart.repository.BookingRepository;
import com.example.parksmart.repository.SlotRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private SlotRepository slotRepository;

    public Booking create(Booking booking) {

        // Check time
        if (!booking.getEndTime().isAfter(booking.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        // Check slot
        if (booking.getSlot() == null ||
                booking.getSlot().getId() == null) {

            throw new IllegalArgumentException(
                    "Please select a valid parking slot");
        }

        Slot slot = slotRepository
                .findById(booking.getSlot().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Parking slot not found"));

        // Check overlapping bookings
        List<Booking> bookings = repository.findAll();

        for (Booking existing : bookings) {

            // Ignore old bookings which have no Slot relationship
            if (existing.getSlot() == null) {
                continue;
            }

            if (existing.getSlot().getId() == null) {
                continue;
            }

            // Same slot + overlapping time
            if (existing.getSlot().getId().equals(slot.getId())
                    && booking.getStartTime()
                            .isBefore(existing.getEndTime())
                    && booking.getEndTime()
                            .isAfter(existing.getStartTime())) {

                throw new IllegalArgumentException(
                        "Slot is already booked for the selected time");
            }
        }

        // Connect booking with slot
        booking.setSlot(slot);

        // Mark slot reserved
        slot.setStatus("RESERVED");
        slotRepository.save(slot);

        return repository.save(booking);
    }

    public List<Booking> getAll() {
        return repository.findAll();
    }

    public Booking getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Booking not found"));
    }
}