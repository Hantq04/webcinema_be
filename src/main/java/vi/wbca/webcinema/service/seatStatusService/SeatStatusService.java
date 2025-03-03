package vi.wbca.webcinema.service.seatStatusService;

import vi.wbca.webcinema.model.SeatStatus;

import java.util.List;

public interface SeatStatusService {
    SeatStatus insertSeatStatus(SeatStatus seatStatus);

    List<SeatStatus> getAllStatus();
}
