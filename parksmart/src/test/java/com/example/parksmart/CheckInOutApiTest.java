package com.example.parksmart;

import com.example.parksmart.entity.Booking;
import com.example.parksmart.entity.ParkingLot;
import com.example.parksmart.entity.Slot;
import com.example.parksmart.repository.BookingRepository;
import com.example.parksmart.repository.CheckInOutRepository;
import com.example.parksmart.repository.ParkingLotRepository;
import com.example.parksmart.repository.SlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CheckInOutApiTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private CheckInOutRepository checkInOutRepository;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();

        checkInOutRepository.deleteAll();
        bookingRepository.deleteAll();
        slotRepository.deleteAll();
        parkingLotRepository.deleteAll();
    }

    @Test
    void shouldAcceptBookingIdForCheckInOutRequest() throws Exception {
        ParkingLot lot = new ParkingLot();
        lot.setName("Main Gate");
        lot.setLocation("North Wing");
        lot.setTotalSlots(10);
        lot = parkingLotRepository.save(lot);

        Slot slot = new Slot();
        slot.setParkingLot(lot);
        slot.setSlotNumber("A1");
        slot.setStatus("RESERVED");
        slot = slotRepository.save(slot);

        Booking booking = new Booking();
        booking.setSlot(slot);
        booking.setVehicleNumber("TN01AB1234");
        booking.setStartTime(LocalDateTime.of(2026, 9, 28, 18, 45));
        booking.setEndTime(LocalDateTime.of(2026, 9, 30, 18, 45));
        booking = bookingRepository.save(booking);

        String payload = """
                {
                  "booking": {"id": %d},
                  "checkInTime": "2026-09-29T18:45:00",
                  "checkOutTime": "2026-09-29T19:46:00"
                }
                """.formatted(booking.getId());

        mockMvc.perform(post("/check-in-out")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated());
    }
}
