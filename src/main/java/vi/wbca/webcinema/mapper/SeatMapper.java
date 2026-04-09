package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.response.SeatResponse;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    @Mapping(source = "seat.id", target = "id")
    @Mapping(source = "seat.line", target = "line")
    @Mapping(source = "seat.number", target = "number")
    @Mapping(source = "seat.seatStatus.nameStatus", target = "status")
    @Mapping(source = "seat.room.code", target = "room")
    @Mapping(source = "seat.seatType.nameType", target = "seatType")
    SeatResponse toSeatResponse(Seat seat);
}
