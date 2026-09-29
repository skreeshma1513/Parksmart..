package com.example.parksmart;

import com.example.parksmart.entity.Booking;
import com.example.parksmart.entity.CheckInOut;
import com.example.parksmart.entity.ParkingLot;
import com.example.parksmart.entity.Slot;
import com.example.parksmart.repository.BookingRepository;
import com.example.parksmart.repository.CheckInOutRepository;
import com.example.parksmart.repository.ParkingLotRepository;
import com.example.parksmart.repository.SlotRepository;
import com.example.parksmart.service.BookingService;
import com.example.parksmart.service.CheckInOutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BusinessRulesTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CheckInOutService checkInOutService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private CheckInOutRepository checkInOutRepository;

    @BeforeEach
    void cleanUp() {
        checkInOutRepository.deleteAll();
        bookingRepository.deleteAll();
        slotRepository.deleteAll();
        parkingLotRepository.deleteAll();
    }

    @Test
    void shouldRejectOverlappingBookingOnSameSlot() {
        ParkingLot lot = saveLot();
        Slot slot = saveSlot(lot, "A1");

        Booking firstBooking = new Booking();
        firstBooking.setSlot(slot);
        firstBooking.setVehicleNumber("TN01AB1234");
        firstBooking.setStartTime(LocalDateTime.of(2026, 9, 28, 9, 0));
        firstBooking.setEndTime(LocalDateTime.of(2026, 9, 28, 12, 0));
        bookingService.create(firstBooking);

        Booking secondBooking = new Booking();
        secondBooking.setSlot(slot);
        secondBooking.setVehicleNumber("TN01CD5678");
        secondBooking.setStartTime(LocalDateTime.of(2026, 9, 28, 10, 30));
        secondBooking.setEndTime(LocalDateTime.of(2026, 9, 28, 13, 0));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.create(secondBooking));

        assertTrue(exception.getMessage().contains("already booked"));
    }

    @Test
    void shouldChargePenaltyForOverstay() {
        ParkingLot lot = saveLot();
        Slot slot = saveSlot(lot, "B2");

        Booking booking = new Booking();
        booking.setSlot(slot);
        booking.setVehicleNumber("TN09ZZ9999");
        booking.setStartTime(LocalDateTime.of(2026, 9, 28, 8, 0));
        booking.setEndTime(LocalDateTime.of(2026, 9, 28, 10, 0));
        booking = bookingService.create(booking);

        CheckInOut checkInOut = new CheckInOut();
        checkInOut.setBooking(booking);
        checkInOut.setCheckInTime(LocalDateTime.of(2026, 9, 28, 8, 15));
        checkInOut.setCheckOutTime(LocalDateTime.of(2026, 9, 28, 11, 30));

        CheckInOut result = checkInOutService.create(checkInOut);

        assertEquals(90.0, result.getPenalty(), 0.01);
    }

    private ParkingLot saveLot() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName("Main Gate");
        parkingLot.setLocation("North Wing");
        parkingLot.setTotalSlots(20);
        return parkingLotRepository.save(parkingLot);
    }

    private Slot saveSlot(ParkingLot parkingLot, String slotNumber) {
        Slot slot = new Slot();
        slot.setParkingLot(parkingLot);
        slot.setSlotNumber(slotNumber);
        slot.setStatus("AVAILABLE");
        return slotRepository.save(slot);
    }
}
