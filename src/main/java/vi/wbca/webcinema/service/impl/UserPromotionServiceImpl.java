package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.enums.PromotionTypeEnum;
import vi.wbca.webcinema.enums.VoucherStatusEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.UserPromotionMapper;
import vi.wbca.webcinema.model.dto.ticket.UserPromotionDTO;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.model.entity.bill.UserPromotion;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.request.SavePromotionRequest;
import vi.wbca.webcinema.model.request.ViewVoucherRequest;
import vi.wbca.webcinema.repository.bill.PromotionRepo;
import vi.wbca.webcinema.repository.bill.UserPromotionRepo;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.service.UserPromotionService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPromotionServiceImpl implements UserPromotionService {
    private final UserPromotionRepo userPromotionRepo;
    private final UserRepo userRepo;
    private final PromotionRepo promotionRepo;
    private final UserPromotionMapper userPromotionMapper;

    @Override
    public void savePromotion(SavePromotionRequest request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        Promotion promotion = promotionRepo.findByCode(request.getPromotionCode())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        if (userPromotionRepo.findByUserIdAndPromotionId(request.getUserId(), promotion.getId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATE_PROMOTION);
        }

        UserPromotion userPromotion = new UserPromotion();
        userPromotion.setUser(user);
        userPromotion.setPromotion(promotion);
        userPromotion.setUsed(false);
        userPromotionRepo.save(userPromotion);
    }

    @Override
    public List<UserPromotionDTO> getUserPromotions(ViewVoucherRequest request) {
        userRepo.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        List<UserPromotion> userPromotions = userPromotionRepo.findByUserId(request.getUserId());
        LocalDateTime now = LocalDateTime.now();
        PromotionTypeEnum promotionType = request.getPromotionType();
        VoucherStatusEnum voucherStatus = request.getVoucherStatus();
        LocalDateTime startTime = request.getStartDate() == null ? null : request.getStartDate().atStartOfDay();
        LocalDateTime endTime = request.getEndDate() == null ? null : request.getEndDate().atTime(LocalTime.MAX);

        return userPromotions.stream()
                .filter(userPromotion -> promotionType == null
                        || userPromotion.getPromotion().getPromotionType() == promotionType)
                .filter(userPromotion -> {
                    if (voucherStatus == null) return true;
                    if (voucherStatus == VoucherStatusEnum.USED) return true;
                    if (userPromotion.isUsed()) return false;
                    boolean expired = userPromotion.getPromotion().getEndTime() != null
                            && userPromotion.getPromotion().getEndTime().isBefore(now);
                    return switch (voucherStatus) {
                        case USED -> true;
                        case UNUSED -> !expired;
                        case EXPIRED -> expired;
                    };
                })
                .filter(userPromotion -> {
                    if (startTime == null && endTime == null) return true;
                    LocalDateTime createdAt = userPromotion.getCreatedAt();
                    if (createdAt == null) return false;
                    boolean notAfterEnd = endTime == null || !createdAt.isAfter(endTime);
                    boolean notBeforeStart = startTime == null || !createdAt.isBefore(startTime);
                    return notBeforeStart && notAfterEnd;
                })
                .sorted(Comparator.comparing(UserPromotion::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .map(userPromotionMapper::toUserPromotionDTO)
                .collect(Collectors.toList());
    }
}
