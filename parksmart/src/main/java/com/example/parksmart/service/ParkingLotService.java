package com.example.parksmart.service;

import com.example.parksmart.entity.ParkingLot;
import com.example.parksmart.repository.ParkingLotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingLotService {

    @Autowired
    private ParkingLotRepository repository;

    public ParkingLot create(ParkingLot parkingLot) {
        return repository.save(parkingLot);
    }

    public List<ParkingLot> getAll() {
        return repository.findAll();
    }
}