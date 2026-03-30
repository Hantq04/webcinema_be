package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.room.SeatDTO;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.response.SeatResponse;

import java.util.List;

public interface SeatService {
    SeatDTO insertSeat(SeatDTO seatDTO);

    void updateSeat(SeatDTO seatDTO);

    void deleteSeat(Long id);

    Seat findById(Long id);

    void refreshSeat(String code);

    List<SeatResponse> getAllSeat();
}
