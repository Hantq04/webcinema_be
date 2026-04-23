package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.dto.cinema.CinemaDTO;
import vi.wbca.webcinema.model.entity.cinema.Cinema;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CinemaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "active", ignore = true)
    Cinema toCinema(CinemaDTO cinemaDTO);

    CinemaDTO toCinemaDTO(Cinema cinema);
}
