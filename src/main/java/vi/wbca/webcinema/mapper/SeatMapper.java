package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.room.SeatDTO;
import vi.wbca.webcinema.model.Seat;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "seatStatus", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "seatType", ignore = true)
    Seat toSeat(SeatDTO seatDTO);

    SeatDTO toSeatDTO(Seat seat);
}
