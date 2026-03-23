package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.room.RoomDTO;

public interface RoomService {
    RoomDTO insertRoom(RoomDTO roomDTO);
    void updateRoom(RoomDTO roomDTO);
    void deleteRoom(String name);
}
