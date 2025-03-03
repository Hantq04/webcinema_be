package vi.wbca.webcinema.service.roomService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.RoomDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.RoomMapper;
import vi.wbca.webcinema.model.Room;
import vi.wbca.webcinema.repository.RoomRepo;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{
    private final RoomRepo roomRepo;
    private final RoomMapper roomMapper;

    @Override
    public RoomDTO insertRoom(RoomDTO request) {
        Room room = roomMapper.toRoom(request);
        room.setActive(true);
        roomRepo.save(room);
        return roomMapper.toRoomDTO(room);
    }

    @Override
    public void updateRoom(RoomDTO request) {
        Room room = roomRepo.findByNameAndCode(request.getName(), request.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        room.setCapacity(request.getCapacity());
        room.setDescription(request.getDescription());
        room.setType(request.getType());
        roomRepo.save(room);
    }

    @Override
    public void deleteRoom(String code) {
        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new AppException((ErrorCode.CODE_NOT_FOUND)));
        roomRepo.delete(room);
    }
}
