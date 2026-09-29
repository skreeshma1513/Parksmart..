package com.example.parksmart;

import com.example.parksmart.entity.ParkingLot;
import com.example.parksmart.repository.ParkingLotRepository;
import com.example.parksmart.repository.SlotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SeedDataTest {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Test
    void shouldSeedChennaiAndCoimbatoreParkingLotsWithFiveSlotsEach() {
        List<ParkingLot> lots = parkingLotRepository.findAll();

        assertTrue(lots.size() >= 2);
        assertTrue(lots.stream().anyMatch(lot -> "Chennai".equals(lot.getLocation())));
        assertTrue(lots.stream().anyMatch(lot -> "Coimbatore".equals(lot.getLocation())));

        long totalSlots = slotRepository.count();
        assertEquals(10, totalSlots);
    }
}
