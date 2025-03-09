package vi.wbca.webcinema.service.promotionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.PromotionDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.PromotionMapper;
import vi.wbca.webcinema.model.Promotion;
import vi.wbca.webcinema.repository.PromotionRepo;

import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService{
    private final PromotionRepo promotionRepo;
    private final PromotionMapper promotionMapper;

    @Override
    public void insertPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = promotionMapper.toPromotion(promotionDTO);
        promotion.setPromotionType(promotionDTO.getPromotionType());

        if (promotionDTO.getStartTime() == null) {
            promotion.setStartTime(new Date());
        } else promotion.setStartTime(promotionDTO.getStartTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(promotion.getStartTime());
        calendar.add(Calendar.HOUR, 24);
        promotion.setEndTime(calendar.getTime());

        promotion.setActive(true);
        promotionRepo.save(promotion);
    }

    @Override
    public void deletePromotion(String name) {
        Promotion promotion = promotionRepo.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        promotionRepo.delete(promotion);
    }
}
