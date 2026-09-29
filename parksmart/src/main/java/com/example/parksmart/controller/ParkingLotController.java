package com.example.parksmart.controller;

import com.example.parksmart.entity.ParkingLot;
import com.example.parksmart.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parking-lots")
public class ParkingLotController {

    @Autowired
    private ParkingLotService service;

    @PostMapping
    public ParkingLot create(@RequestBody ParkingLot parkingLot) {
        return service.create(parkingLot);
    }

    @GetMapping
    public List<ParkingLot> getAll() {
        return service.getAll();
    }
}