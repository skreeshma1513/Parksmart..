package com.example.parksmart.controller;

import com.example.parksmart.entity.Slot;
import com.example.parksmart.service.SlotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/slots")
public class SlotController {

    @Autowired
    private SlotService service;

    @PostMapping
    public Slot create(@RequestBody Slot slot) {
        return service.create(slot);
    }

    @GetMapping
    public List<Slot> getAll() {
        return service.getAll();
    }

    @GetMapping("/available")
    public List<Slot> getAvailableSlots(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {

        return service.getAvailableSlots(
                startTime,
                endTime);
    }

    @GetMapping("/occupancy")
    public Map<String, Object> getOccupancy() {
        return service.getOccupancy();
    }
}