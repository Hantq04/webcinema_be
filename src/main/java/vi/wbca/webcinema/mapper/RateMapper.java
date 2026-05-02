package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.entity.movie.Rate;
import vi.wbca.webcinema.model.response.RateResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RateMapper {
    RateResponse toRateResponse(Rate rate);
}