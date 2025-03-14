package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.PromotionDTO;
import vi.wbca.webcinema.model.Promotion;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bills", ignore = true)
    Promotion toPromotion(PromotionDTO promotionDTO);

    PromotionDTO toPromotionDTO(Promotion promotion);
}
