package com.example.parksmart.config;

import com.example.parksmart.entity.ParkingLot;
import com.example.parksmart.entity.Slot;
import com.example.parksmart.repository.ParkingLotRepository;
import com.example.parksmart.repository.SlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ParkingLotRepository parkingLotRepository;
    private final SlotRepository slotRepository;

    public DataSeeder(ParkingLotRepository parkingLotRepository,
            SlotRepository slotRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.slotRepository = slotRepository;
    }

    @Override
    public void run(String... args) {
        if (parkingLotRepository.count() > 0) {
            return;
        }

        seedParkingLot("Chennai Central Parking", "Chennai", 5);
        seedParkingLot("Coimbatore Smart Park", "Coimbatore", 5);
    }

    private void seedParkingLot(String name, String location, int slotCount) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(name);
        parkingLot.setLocation(location);
        parkingLot.setTotalSlots(slotCount);
        ParkingLot savedLot = parkingLotRepository.save(parkingLot);

        List<String> slotNumbers = List.of("A1", "A2", "A3", "A4", "A5");

        for (int i = 0; i < slotCount; i++) {
            Slot slot = new Slot();
            slot.setParkingLot(savedLot);
            slot.setSlotNumber(slotNumbers.get(i));
            slot.setStatus("AVAILABLE");
            slotRepository.save(slot);
        }
    }
}
