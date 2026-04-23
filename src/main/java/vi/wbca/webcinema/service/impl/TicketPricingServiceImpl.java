package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.enums.RoomTypeEnum;
import vi.wbca.webcinema.enums.SeatTypeEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.setting.GeneralSetting;
import vi.wbca.webcinema.repository.setting.GeneralSettingRepo;
import vi.wbca.webcinema.service.TicketPricingService;

import java.time.DayOfWeek;

@Service
@RequiredArgsConstructor
public class TicketPricingServiceImpl implements TicketPricingService {
    private final GeneralSettingRepo generalSettingRepo;

    @Override
    public Long calculateFinalPrice(Schedule schedule, String seatTypeName) {
        double seatPrice = SeatTypeEnum.getPriceByType(normalizeSeatType(seatTypeName));
        GeneralSetting setting = generalSetting();
        double roomMultiplier = getRoomPriceMultiplier(schedule);

        String showTimeName = schedule.getName();
        double discount = switch (showTimeName) {
            case "MORNING" -> 0.15;
            case "NOON" -> 0.10;
            case "AFTERNOON" -> 0.05;
            case "EVENING" -> 0.0;
            case "LATE_NIGHT" -> 0.20;
            default -> throw new AppException(ErrorCode.INVALID_SHOW_TIME);
        };

        DayOfWeek dayOfWeek = schedule.getStartAt().getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;

        double finalPrice = seatPrice * roomMultiplier;
        finalPrice *= (isWeekend ? (1 + setting.getPercentWeekend() / 100.0) : 1);
        finalPrice *= (1 - discount);

        return Math.round(finalPrice);
    }

    private String normalizeSeatType(String seatTypeName) {
        if (seatTypeName == null) {
            throw new AppException(ErrorCode.INVALID_SEAT);
        }
        return seatTypeName.trim().toUpperCase().replace(' ', '_');
    }

    private double getRoomPriceMultiplier(Schedule schedule) {
        Room room = schedule.getRoom();
        if (room == null || room.getType() == null) {
            throw new AppException(ErrorCode.TYPE_NOT_FOUND);
        }
        RoomTypeEnum roomType = room.getType();
        return roomType.getPriceMultiplier();
    }

    private GeneralSetting generalSetting() {
        return generalSettingRepo.findTopByOrderByIdDesc()
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
    }
}
