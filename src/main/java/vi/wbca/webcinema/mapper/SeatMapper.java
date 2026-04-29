package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.response.SeatResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeatMapper {
    @Mapping(source = "seat.id", target = "id")
    @Mapping(source = "seat.line", target = "line")
    @Mapping(source = "seat.number", target = "number")
    @Mapping(source = "seat.seatStatus.code", target = "status")
    @Mapping(source = "seat.room.code", target = "room")
    @Mapping(source = "seat.seatType.nameType", target = "seatType")
    SeatResponse toSeatResponse(Seat seat);
}
