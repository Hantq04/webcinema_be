package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.dto.room.RoomDTO;
import vi.wbca.webcinema.model.entity.cinema.Room;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "seats", ignore = true)
    @Mapping(target = "cinema", ignore = true)
    Room toRoom(RoomDTO roomDTO);

    RoomDTO toRoomDTO(Room room);
}
