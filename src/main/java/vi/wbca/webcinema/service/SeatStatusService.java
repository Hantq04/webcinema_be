package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.seat.SeatStatus;

import java.util.List;

public interface SeatStatusService {
    SeatStatus insertSeatStatus(SeatStatus seatStatus);

    List<SeatStatus> getAllStatus();
}
