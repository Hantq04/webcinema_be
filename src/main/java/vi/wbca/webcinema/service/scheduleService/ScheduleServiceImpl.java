package vi.wbca.webcinema.service.scheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.ScheduleDTO;
import vi.wbca.webcinema.enums.ESeatType;
import vi.wbca.webcinema.enums.ShowTime;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.ScheduleMapper;
import vi.wbca.webcinema.model.Movie;
import vi.wbca.webcinema.model.Room;
import vi.wbca.webcinema.model.Schedule;
import vi.wbca.webcinema.repository.MovieRepo;
import vi.wbca.webcinema.repository.RoomRepo;
import vi.wbca.webcinema.repository.ScheduleRepo;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.util.Calendar;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepo scheduleRepo;
    private final ScheduleMapper scheduleMapper;
    private final MovieRepo movieRepo;
    private final RoomRepo roomRepo;

    @Override
    public ScheduleDTO insertSchedule(ScheduleDTO scheduleDTO) {
        Room room = setRoom(scheduleDTO);

        if (scheduleRepo.existsByRoomAndStartAt(room, scheduleDTO.getStartAt())) {
            throw new AppException(ErrorCode.DUPLICATE_SHOWTIME);
        }

        Schedule schedule = scheduleMapper.toSchedule(scheduleDTO);
        Movie movie = setMovie(scheduleDTO);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getStartAt());
        calendar.add(Calendar.MINUTE, movie.getMovieDuration());

        setName(schedule);
        schedule.setPrice(calculateFinalPrice(schedule, scheduleDTO.getSeatTypeId()));
        schedule.setEndAt(calendar.getTime());
        schedule.setCode(GenerateCode.generateCode());
        schedule.setActive(true);
        schedule.setMovie(movie);
        schedule.setRoom(room);

        scheduleRepo.save(schedule);
        return scheduleMapper.toScheduleDTO(schedule);
    }

    @Override
    public void updateSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepo.findByCode(scheduleDTO.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        schedule.setStartAt(scheduleDTO.getStartAt());
        scheduleRepo.save(schedule);
    }

    public void setName(Schedule schedule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getStartAt());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Automatically set name based on showtime
        if (hour >= 8 && hour < 11) schedule.setName(ShowTime.MORNING.toString());
        else if (hour >= 11 && hour < 14) schedule.setName(ShowTime.NOON.toString());
        else if (hour >= 14 && hour < 17) schedule.setName(ShowTime.AFTERNOON.toString());
        else if (hour >= 17 && hour < 22) schedule.setName(ShowTime.EVENING.toString());
        else schedule.setName(ShowTime.LATE_NIGHT.toString());
    }

    public Double calculateFinalPrice(Schedule schedule, int seatTypeId) {
        double basePrice = getSeatPrice(seatTypeId);
        String showTimeName = schedule.getName();

        double discount = switch (showTimeName) {
            // The price is discounted based on the showtime
            case "MORNING" -> 0.20;
            case "NOON" -> 0.15;
            case "AFTERNOON" -> 0.10;
            case "EVENING" -> 0.0;
            case "LATE_NIGHT" -> 0.25;
            default -> throw new AppException(ErrorCode.INVALID_SHOW_TIME);
        };

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getStartAt());
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        if (week == Calendar.SATURDAY || week == Calendar.SUNDAY) {
            // The price increases by 10% on weekends
            basePrice *= 1.2;
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
