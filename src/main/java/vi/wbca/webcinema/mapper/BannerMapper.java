package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.entity.setting.Banner;
import vi.wbca.webcinema.model.response.BannerResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BannerMapper {
    BannerResponse toBannerResponse(Banner banner);
}