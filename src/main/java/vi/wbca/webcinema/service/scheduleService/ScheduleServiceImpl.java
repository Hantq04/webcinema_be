package vi.wbca.webcinema.service.scheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.ScheduleDTO;
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
public class ScheduleServiceImpl implements ScheduleService{
    private final ScheduleRepo scheduleRepo;
    private final ScheduleMapper scheduleMapper;
    private final MovieRepo movieRepo;
    private final RoomRepo roomRepo;

    @Override
    public ScheduleDTO insertSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleMapper.toSchedule(scheduleDTO);
        Movie movie = setMovie(scheduleDTO);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getStartAt());
        calendar.add(Calendar.MINUTE, movie.getMovieDuration());

        schedule.setEndAt(calendar.getTime());
        schedule.setCode(GenerateCode.generateCode());
        setName(schedule);
        schedule.setActive(true);
        schedule.setMovie(movie);
        schedule.setRoom(setRoom(scheduleDTO));
        scheduleRepo.save(schedule);

        return scheduleMapper.toScheduleDTO(schedule);
    }

    @Override
    public void updateSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepo.findByName(scheduleDTO.getName())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        schedule.setPrice(scheduleDTO.getPrice());
        schedule.setStartAt(scheduleDTO.getStartAt());
        scheduleRepo.save(schedule);
    }

    public void setName(Schedule schedule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getStartAt());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 8 && hour < 11) {
            schedule.setName(ShowTime.MORNING.name());
        } else if (hour >= 11 && hour < 14) {
            schedule.setName(ShowTime.NOON.name());
        } else if (hour >= 14 && hour < 17) {
            schedule.setName(ShowTime.AFTERNOON.name());
        } else if (hour >= 17 && hour < 22) {
            schedule.setName(ShowTime.EVENING.name());
        } else {
            schedule.setName(ShowTime.LATE_NIGHT.name());
        }
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
    public void deleteSchedule(String name, Long movieId) {
        Schedule schedule = scheduleRepo.findByNameAndMovieId(name, movieId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        scheduleRepo.delete(schedule);
    }
}
