package com.example.parksmart.service;

import com.example.parksmart.entity.Booking;
import com.example.parksmart.entity.Slot;
import com.example.parksmart.repository.BookingRepository;
import com.example.parksmart.repository.SlotRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SlotService {

    @Autowired
    private SlotRepository repository;

    @Autowired
    private BookingRepository bookingRepository;

    public Slot create(Slot slot) {

        if (slot.getStatus() == null ||
                slot.getStatus().isBlank()) {

            slot.setStatus("AVAILABLE");
        }

        return repository.save(slot);
    }

    public List<Slot> getAll() {
        return repository.findAll();
    }

    // Available slots for a selected time window
    public List<Slot> getAvailableSlots(
            LocalDateTime startTime,
            LocalDateTime endTime) {

        if (!endTime.isAfter(startTime)) {
            throw new RuntimeException(
                    "End time must be after start time");
        }

        List<Slot> allSlots = repository.findAll();
        List<Booking> bookings = bookingRepository.findAll();

        return allSlots.stream()
                .filter(slot -> {

                    return bookings.stream().noneMatch(booking ->

                booking.getSlot() != null
                        && booking.getSlot().getId()
                                .equals(slot.getId())

                        && startTime.isBefore(
                                booking.getEndTime())

                        && endTime.isAfter(
                                booking.getStartTime()));
                })
                .collect(Collectors.toList());
    }

    // Real-time occupancy
    public Map<String, Object> getOccupancy() {

        List<Slot> slots = repository.findAll();

        long totalSlots = slots.size();

        long occupiedSlots = slots.stream()
                .filter(slot -> "RESERVED".equalsIgnoreCase(
                        slot.getStatus()))
                .count();

        long availableSlots = totalSlots - occupiedSlots;

        Map<String, Object> result = new HashMap<>();

        result.put("totalSlots", totalSlots);
        result.put("occupiedSlots", occupiedSlots);
        result.put("availableSlots", availableSlots);

        return result;
    }
}