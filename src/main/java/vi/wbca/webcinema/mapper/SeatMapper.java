package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.SeatDTO;
import vi.wbca.webcinema.model.Seat;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    Seat toSeat(SeatDTO seatDTO);

    SeatDTO toSeatDTO(Seat seat);
}
