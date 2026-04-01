package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.room.SeatDTO;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.response.SeatResponse;

import java.util.List;
import java.util.Map;

public interface SeatService {
    void insertSeat(SeatDTO seatDTO);

    void updateSeat(SeatDTO seatDTO);

    void deleteSeat(Long id);

    Seat findById(Long id);

    void refreshSeat(String code);

    List<SeatResponse> getAllSeat();

    void validateSeatSelection(List<Seat> seats);

    Map<String, Object> getSeatBySchedule(String scheduleCode);
}
