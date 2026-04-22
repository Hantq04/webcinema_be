package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.dto.schedule.ScheduleDTO;
import vi.wbca.webcinema.enums.ShowTimeEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.ScheduleMapper;
import vi.wbca.webcinema.model.entity.setting.GeneralSetting;
import vi.wbca.webcinema.model.entity.movie.Movie;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.response.ScheduleResponse;
import vi.wbca.webcinema.model.response.ScheduleGroupByDateResponse;
import vi.wbca.webcinema.model.response.CinemaScheduleResponse;
import vi.wbca.webcinema.model.request.ScheduleMovieFilterRequest;
import vi.wbca.webcinema.repository.setting.GeneralSettingRepo;
import vi.wbca.webcinema.repository.movie.MovieRepo;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.movie.ScheduleRepo;
import vi.wbca.webcinema.service.ScheduleService;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

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
        Room room = roomRepo.findByNameAndCode(scheduleDTO.getRoomName(), scheduleDTO.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        Movie movie = movieRepo.findByNameAndIsActive(scheduleDTO.getMovieName(), true)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        LocalDateTime startAt = scheduleDTO.getStartAt();

        if (startAt.isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_DATE);
        }
        if (movie.getEndDate() != null && movie.getEndDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.MOVIE_EXPIRED);
        }
        
        startAt = checkLastEndAt(room.getId(), startAt);
        LocalDateTime endAt = setEndTime(startAt, movie.getMovieDuration());

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

    @Override
    public void updateSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepo.findByCode(scheduleDTO.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        schedule.setStartAt(scheduleDTO.getStartAt());
        scheduleRepo.save(schedule);
    }

    @Override
    public void deactivateExpiredSchedule() {
        List<Schedule> expiredSchedule = scheduleRepo.findAllByEndAtBeforeAndIsActiveTrue(LocalDateTime.now());
        for (Schedule schedule: expiredSchedule) {
            schedule.setActive(false);
        }
        scheduleRepo.saveAll(expiredSchedule);
    }

    @Override
    public void deleteSchedule(String code, Long movieId) {
        Schedule schedule = scheduleRepo.findByCodeAndMovieId(code, movieId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        scheduleRepo.delete(schedule);
    }

    @Override
    public List<ScheduleResponse> getAllSchedule() {
        return scheduleRepo.findAll().stream()
                .map(scheduleMapper::toScheduleResponse)
                .toList();
    }

    @Override
        public List<ScheduleGroupByDateResponse> getSchedulesByMovieGroupedByDate(ScheduleMovieFilterRequest request) {
        List<Schedule> schedules = scheduleRepo.findByMovieId(request.getMovieId());
        String address = request.getAddress() == null ? null : request.getAddress().trim();
        String roomType = request.getRoomType() == null ? null : request.getRoomType().trim();

        Map<String, Map<String, List<Schedule>>> groupedByDateAndRoom = schedules.stream()
            .filter(Schedule::isActive)
            .filter(schedule -> !StringUtils.hasText(address)
                || (schedule.getRoom().getCinema().getAddress() != null
                && schedule.getRoom().getCinema().getAddress().trim().equalsIgnoreCase(address)))
            .filter(schedule -> !StringUtils.hasText(roomType)
                || (schedule.getRoom().getType() != null
                && schedule.getRoom().getType().name().equalsIgnoreCase(roomType)))
            .collect(Collectors.groupingBy(
                schedule -> schedule.getStartAt().toLocalDate().toString(),
                LinkedHashMap::new,
                Collectors.groupingBy(
                    schedule -> schedule.getRoom().getCinema().getId()
                        + "|" + schedule.getRoom().getCode()
                        + "|" + (schedule.getRoom().getType() != null ? schedule.getRoom().getType().name() : ""),
                    LinkedHashMap::new,
                    Collectors.toList()
                )
            ));

        return groupedByDateAndRoom.entrySet().stream()
                .map(dateEntry -> {
                List<CinemaScheduleResponse> cinemaSchedules = dateEntry.getValue().entrySet().stream()
                    .map(roomEntry -> {
                    List<Schedule> roomSchedulesList = roomEntry.getValue();
                    Schedule firstSchedule = roomSchedulesList.get(0);
                    List<String> showtimeList = roomSchedulesList.stream()
                        .map(schedule -> schedule.getStartAt().toLocalTime().toString())
                        .distinct()
                        .sorted()
                        .toList();

                    return CinemaScheduleResponse.builder()
                        .cinemaId(firstSchedule.getRoom().getCinema().getId())
                        .cinemaName(firstSchedule.getRoom().getCinema().getNameOfCinema())
                        .roomCode(firstSchedule.getRoom().getCode())
                        .roomType(firstSchedule.getRoom().getType() != null ? firstSchedule.getRoom().getType().name() : null)
                        .sometimes(showtimeList)
                        .build();
                    })
                    .toList();

                    return ScheduleGroupByDateResponse.builder()
                    .date(dateEntry.getKey())
                            .cinemas(cinemaSchedules)
                            .build();
                }).toList();
    }

    public LocalDateTime setEndTime(LocalDateTime startAt, int duration) {
        LocalDateTime endTime = startAt.plusMinutes(duration);
        return roundUpToNearestFiveMinutes(endTime);
    }

    public LocalDateTime checkLastEndAt(Long roomId, LocalDateTime startAt) {
        LocalDateTime lastEndAt = scheduleRepo.findLastEndAt(roomId, startAt);

        if (lastEndAt != null) {
            long diffMinutes = java.time.temporal.ChronoUnit.MINUTES.between(lastEndAt, startAt);

            // Adjust startAt to start 20 minutes later
            if (diffMinutes < 20) {
                LocalDateTime newStartAt = lastEndAt.plusMinutes(20);
                return roundUpToNearestFiveMinutes(newStartAt);
            }
        }
        return roundUpToNearestFiveMinutes(startAt);
    }

    public LocalDateTime roundUpToNearestFiveMinutes(LocalDateTime dateTime) {
        int minutes = dateTime.getMinute();
        // Round up time to the nearest 5 minutes
        int roundedMinutes = ((minutes + 4) / 5) * 5;
        int addedMinutes = roundedMinutes - minutes;
        return dateTime.plusMinutes(addedMinutes).withSecond(0).withNano(0);
    }

    public void setName(Schedule schedule, Movie movie) {
        LocalDateTime startAt = schedule.getStartAt();
        int startHour = getHour(startAt);
        int endHour = getHour(setEndTime(startAt, movie.getMovieDuration()));

        GeneralSetting setting = generalSettingRepo.findTopByOrderByIdDesc()
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));

        // Only apply if startAt greater than timeBeginToChange
        if (schedule.getStartAt().isAfter(setting.getTimeBeginToChange())) {
            validateScheduleTime(startHour, endHour, setting);
        }

        breakTime(schedule, setting);
        // Automatically set name based on showtime
        schedule.setName(getShowTimeName(startHour));
    }

    public int getHour(LocalDateTime dateTime) {
        return dateTime.getHour();
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
        if (hour >= 8 && hour < 11) return ShowTimeEnum.MORNING.toString();
        if (hour >= 11 && hour < 14) return ShowTimeEnum.NOON.toString();
        if (hour >= 14 && hour < 17) return ShowTimeEnum.AFTERNOON.toString();
        if (hour >= 17 && hour < 22) return ShowTimeEnum.EVENING.toString();
        return ShowTimeEnum.LATE_NIGHT.toString();
    }

    public void breakTime(Schedule schedule, GeneralSetting setting) {
        LocalDateTime startAt = schedule.getStartAt();
        int startHour = startAt.getHour();

        int breakTime = setting.getBreakTime().getHour();
        if (startHour == breakTime) {
            throw new AppException(ErrorCode.SHOW_TIME_IN_BREAK);
        }
    }
}
