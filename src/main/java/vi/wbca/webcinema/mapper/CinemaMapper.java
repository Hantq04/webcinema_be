package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.cinema.CinemaDTO;
import vi.wbca.webcinema.model.cinema.Cinema;

@Mapper(componentModel = "spring")
public interface CinemaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "active", ignore = true)
    Cinema toCinema(CinemaDTO cinemaDTO);

    CinemaDTO toCinemaDTO(Cinema cinema);
}
