package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.schedule.ScheduleDTO;
import vi.wbca.webcinema.enums.ShowTime;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.ScheduleMapper;
import vi.wbca.webcinema.model.setting.GeneralSetting;
import vi.wbca.webcinema.model.movie.Movie;
import vi.wbca.webcinema.model.cinema.Room;
import vi.wbca.webcinema.model.movie.Schedule;
import vi.wbca.webcinema.repository.setting.GeneralSettingRepo;
import vi.wbca.webcinema.repository.movie.MovieRepo;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.movie.ScheduleRepo;
import vi.wbca.webcinema.service.ScheduleService;
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
        Schedule schedule = scheduleMapper.toSchedule(scheduleDTO);
        Room room = setRoom(scheduleDTO);
        Movie movie = setMovie(scheduleDTO);

        Date startAt = scheduleDTO.getStartAt();
        startAt = checkLastEndAt(room.getId(), startAt);
        Date endAt = setEndTime(startAt, movie.getMovieDuration());

        if (scheduleRepo.countByRoomAndTimeOverlap(room, startAt, endAt) > 0) {
            throw new AppException(ErrorCode.DUPLICATE_SHOWTIME);
        }

        setName(schedule, movie);
        schedule.setStartAt(startAt);
        schedule.setEndAt(endAt);
        schedule.setCode(GenerateCode.generateCode());
        schedule.setActive(true);
        schedule.setMovie(movie);
        schedule.setRoom(room);

        scheduleRepo.save(schedule);
        deactivateExpiredSchedule();
        return scheduleMapper.toScheduleDTO(schedule);
    }

    public Date setEndTime(Date startAt, int duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startAt);
        calendar.add(Calendar.MINUTE, duration);
        roundUpToNearestFiveMinutes(calendar);
        return calendar.getTime();
    }

    public Date checkLastEndAt(Long roomId, Date startAt) {
        Date lastEndAt = scheduleRepo.findLastEndAt(roomId, startAt);

        if (lastEndAt != null) {
            long diffMinutes = (startAt.getTime() - lastEndAt.getTime()) / (60 * 1000);

            // Adjust startAt to start 20 minutes later
            if (diffMinutes < 20) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastEndAt);
                calendar.add(Calendar.MINUTE, 20);

                roundUpToNearestFiveMinutes(calendar);
                return calendar.getTime();
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startAt);

        roundUpToNearestFiveMinutes(calendar);
        return calendar.getTime();
    }

    public void roundUpToNearestFiveMinutes(Calendar calendar) {
        int minutes = calendar.get(Calendar.MINUTE);
        // Round up time to the nearest 5 minutes
        int roundedMinutes = ((minutes + 4) / 5) * 5;
        calendar.set(Calendar.MINUTE, roundedMinutes);
        calendar.set(Calendar.SECOND, 0);
    }

    @Override
    public void updateSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepo.findByCode(scheduleDTO.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        schedule.setStartAt(scheduleDTO.getStartAt());
        scheduleRepo.save(schedule);
    }

    public void setName(Schedule schedule, Movie movie) {
        Date startAt = schedule.getStartAt();
        int startHour = getHour(startAt);
        int endHour = getHour(setEndTime(startAt, movie.getMovieDuration()));

        GeneralSetting setting = generalSetting();

        // Only apply if startAt greater than timeBeginToChange
        if (schedule.getStartAt().after(setting.getTimeBeginToChange())) {
            validateScheduleTime(startHour, endHour, setting);
        }

        breakTime(schedule, setting);
        // Automatically set name based on showtime
        schedule.setName(getShowTimeName(startHour));
    }

    public int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public void validateScheduleTime(int startHour, int endHour, GeneralSetting setting) {
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

    private String getShowTimeName(int hour) {
        if (hour >= 8 && hour < 11) return ShowTime.MORNING.toString();
        if (hour >= 11 && hour < 14) return ShowTime.NOON.toString();
        if (hour >= 14 && hour < 17) return ShowTime.AFTERNOON.toString();
        if (hour >= 17 && hour < 22) return ShowTime.EVENING.toString();
        return ShowTime.LATE_NIGHT.toString();
    }

    public void breakTime(Schedule schedule, GeneralSetting setting) {
        Date startAt = schedule.getStartAt();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startAt);
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);

        int breakTime = setting.getBreakTime().getHour();
        if (startHour == breakTime) {
            throw new AppException(ErrorCode.SHOW_TIME_IN_BREAK);
        }
    }

    public GeneralSetting generalSetting() {
        return generalSettingRepo.findTopByOrderByIdDesc()
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
    }

    @Override
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
