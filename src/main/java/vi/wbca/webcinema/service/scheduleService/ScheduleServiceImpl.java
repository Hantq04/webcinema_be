package vi.wbca.webcinema.service.scheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.ScheduleDTO;
import vi.wbca.webcinema.enums.ESeatType;
import vi.wbca.webcinema.enums.ShowTime;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.ScheduleMapper;
import vi.wbca.webcinema.model.GeneralSetting;
import vi.wbca.webcinema.model.Movie;
import vi.wbca.webcinema.model.Room;
import vi.wbca.webcinema.model.Schedule;
import vi.wbca.webcinema.repository.GeneralSettingRepo;
import vi.wbca.webcinema.repository.MovieRepo;
import vi.wbca.webcinema.repository.RoomRepo;
import vi.wbca.webcinema.repository.ScheduleRepo;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepo scheduleRepo;
    private final ScheduleMapper scheduleMapper;
    private final MovieRepo movieRepo;
    private final RoomRepo roomRepo;
    private final GeneralSettingRepo generalSettingRepo;

    @Override
    public ScheduleDTO insertSchedule(ScheduleDTO scheduleDTO) {
        Room room = setRoom(scheduleDTO);

        if (scheduleRepo.existsByRoomAndStartAt(room, scheduleDTO.getStartAt())) {
            throw new AppException(ErrorCode.DUPLICATE_SHOWTIME);
        }
        if (scheduleDTO.getStartAt().before(new Date())) {
            throw new AppException(ErrorCode.INVALID_START_TIME);
        }

        Schedule schedule = scheduleMapper.toSchedule(scheduleDTO);
        Movie movie = setMovie(scheduleDTO);

        setName(schedule, movie);
        schedule.setPrice(calculateFinalPrice(schedule, scheduleDTO.getSeatTypeId()));
        schedule.setEndAt(setEndTime(schedule, movie).getTime());
        schedule.setCode(GenerateCode.generateCode());
        schedule.setActive(true);
        schedule.setMovie(movie);
        schedule.setRoom(room);

        scheduleRepo.save(schedule);
        deactivateExpiredSchedule();
        return scheduleMapper.toScheduleDTO(schedule);
    }

    public Calendar setEndTime(Schedule schedule, Movie movie) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getStartAt());
        calendar.add(Calendar.MINUTE, movie.getMovieDuration());
        return calendar;
    }

    @Override
    public void updateSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepo.findByCode(scheduleDTO.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        schedule.setStartAt(scheduleDTO.getStartAt());
        scheduleRepo.save(schedule);
    }

    public void setName(Schedule schedule, Movie movie) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getStartAt());
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(setEndTime(schedule, movie).getTime());
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);

        GeneralSetting setting = generalSetting();

        // Only apply if startAt greater than timeBeginToChange
        if (schedule.getStartAt().after(setting.getTimeBeginToChange())) {
            int openHour = setting.getOpenTime().getHour();
            int closeHour = setting.getCloseTime().getHour();

            if (closeHour < openHour) {
                // Close time is next day
                if (!((startHour >= openHour || startHour < closeHour) &&
                        (endHour >= openHour || endHour < closeHour))) {
                    throw new AppException(ErrorCode.INVALID_SHOW_TIME);
                }
            } else {
                // Close time is same day
                if (startHour < openHour || startHour >= closeHour ||
                        endHour < openHour || endHour > closeHour) {
                    throw new AppException(ErrorCode.INVALID_SHOW_TIME);
                }
            }
        }

        // Automatically set name based on showtime
        if (startHour >= 8 && startHour < 11) schedule.setName(ShowTime.MORNING.toString());
        else if (startHour >= 11 && startHour < 14) schedule.setName(ShowTime.NOON.toString());
        else if (startHour >= 14 && startHour < 17) schedule.setName(ShowTime.AFTERNOON.toString());
        else if (startHour >= 17 && startHour < 22) schedule.setName(ShowTime.EVENING.toString());
        else schedule.setName(ShowTime.LATE_NIGHT.toString());
    }

    public Double calculateFinalPrice(Schedule schedule, int seatTypeId) {
        GeneralSetting setting = generalSetting();
        double basePrice = getSeatPrice(seatTypeId);
        String showTimeName = schedule.getName();
        double discount = switch (showTimeName) {
            // The price is discounted based on the showtime
            case "MORNING" -> 0.15;
            case "NOON" -> 0.10;
            case "AFTERNOON" -> 0.05;
            case "EVENING" -> 0.0;
            case "LATE_NIGHT" -> 0.20;
            default -> throw new AppException(ErrorCode.INVALID_SHOW_TIME);
        };

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getStartAt());
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        if (week == Calendar.SATURDAY || week == Calendar.SUNDAY) {
            // The price increases by 20% on weekends
            basePrice *= setting.getPercentWeekend();
        }
        return (int) basePrice * (1 - discount);
    }

    public Double getSeatPrice(int seatTypeId) {
        String seatTypeName = switch (seatTypeId) {
            case 1 -> "STANDARD";
            case 2 -> "VIP";
            case 3 -> "DELUXE";
            default -> throw new AppException(ErrorCode.INVALID_SEAT);
        };
        return ESeatType.getPriceByType(seatTypeName);
    }

    public GeneralSetting generalSetting() {
        return generalSettingRepo.findTopByOrderByIdDesc()
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
    }

    public void deactivateExpiredSchedule() {
        List<Schedule> expiredSchedule = scheduleRepo.findAllByEndAtBeforeAndIsActiveTrue(new Date());
        for (Schedule schedule: expiredSchedule) {
            schedule.setActive(false);
        }
        scheduleRepo.saveAll(expiredSchedule);
    }

    public Movie setMovie(ScheduleDTO scheduleDTO) {
        return movieRepo.findByName(scheduleDTO.getMovieName())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    public Room setRoom(ScheduleDTO scheduleDTO) {
        return roomRepo.findByNameAndCode(scheduleDTO.getRoomName(), scheduleDTO.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
    }

    @Override
    public void deleteSchedule(String code, Long movieId) {
        Schedule schedule = scheduleRepo.findByCodeAndMovieId(code, movieId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        scheduleRepo.delete(schedule);
    }
}
