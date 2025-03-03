package vi.wbca.webcinema.service.roomService;

import vi.wbca.webcinema.dto.RoomDTO;
import vi.wbca.webcinema.model.Room;

public interface RoomService {
    RoomDTO insertRoom(RoomDTO roomDTO);
    void updateRoom(RoomDTO roomDTO);
    void deleteRoom(String name);
}
