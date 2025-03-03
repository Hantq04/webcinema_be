package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.RoomDTO;
import vi.wbca.webcinema.model.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "seats", ignore = true)
    Room toRoom(RoomDTO roomDTO);

    RoomDTO toRoomDTO(Room room);
}
