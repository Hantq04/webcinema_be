package vi.wbca.webcinema.service;

import vi.wbca.webcinema.dto.ticket.PromotionDTO;
import vi.wbca.webcinema.model.bill.Promotion;

import java.util.List;

public interface PromotionService {
    void insertPromotion(PromotionDTO promotionDTO);

    void deletePromotion(String name);

    List<Promotion> getAllPromotion();
}
