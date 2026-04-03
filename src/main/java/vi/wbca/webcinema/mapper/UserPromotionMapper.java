package vi.wbca.webcinema.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vi.wbca.webcinema.enums.VoucherStatusEnum;
import vi.wbca.webcinema.model.dto.ticket.UserPromotionDTO;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.model.entity.bill.UserPromotion;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserPromotionMapper {
    
    public UserPromotionDTO toUserPromotionDTO(UserPromotion userPromotion) {
        if (userPromotion == null) {
            return null;
        }
        
        UserPromotionDTO dto = new UserPromotionDTO();
        dto.setUserId(userPromotion.getUser().getId());
        dto.setPromotionId(userPromotion.getPromotion().getId());
        dto.setIsUsed(userPromotion.isUsed());
        dto.setUsedAt(userPromotion.getUsedAt());
        dto.setSaveAt(userPromotion.getCreatedAt());
        
        Promotion promotion = userPromotion.getPromotion();
        dto.setPromotionCode(promotion.getCode());
        dto.setPromotionName(promotion.getName());
        dto.setPromotionPercent(promotion.getPercent());
        dto.setPromotionType(promotion.getPromotionType());
        dto.setPromotionStartTime(promotion.getStartTime());
        dto.setPromotionEndTime(promotion.getEndTime());
        dto.setPromotionDescription(promotion.getDescription());
        dto.setPromotionActive(promotion.isActive());
        dto.setVoucherStatus(resolveVoucherStatus(userPromotion, LocalDateTime.now()));
        
        return dto;
    }

    private VoucherStatusEnum resolveVoucherStatus(UserPromotion userPromotion, LocalDateTime now) {
        if (userPromotion == null || userPromotion.getPromotion() == null) {
            return null;
        }
        if (userPromotion.isUsed()) {
            return null;
        }
        return userPromotion.getPromotion().getEndTime() != null && userPromotion.getPromotion().getEndTime().isBefore(now)
                ? VoucherStatusEnum.EXPIRED
                : VoucherStatusEnum.UNUSED;
    }
}
