package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.room.RoomDTO;
import java.util.List;

public interface RoomService {
    RoomDTO insertRoom(RoomDTO roomDTO);

    void updateRoom(RoomDTO roomDTO);

    void deleteRoom(String name);

    List<String> getRoomCodesByCinema(String cinemaName);
}
