package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.ticket.PromotionDTO;
import vi.wbca.webcinema.model.bill.Promotion;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bills", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "rankCustomer", ignore = true)
    Promotion toPromotion(PromotionDTO promotionDTO);

    @Mapping(target = "nameRankCustomer", ignore = true)
    PromotionDTO toPromotionDTO(Promotion promotion);
}
