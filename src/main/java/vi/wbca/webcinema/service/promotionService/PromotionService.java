package vi.wbca.webcinema.service.promotionService;

import vi.wbca.webcinema.dto.ticket.PromotionDTO;

public interface PromotionService {
    void insertPromotion(PromotionDTO promotionDTO);

    void deletePromotion(String name);
}
