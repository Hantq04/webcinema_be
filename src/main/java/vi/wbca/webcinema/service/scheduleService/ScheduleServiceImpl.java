package vi.wbca.webcinema.service.scheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.ScheduleDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.ScheduleMapper;
import vi.wbca.webcinema.model.Schedule;
import vi.wbca.webcinema.repository.ScheduleRepo;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{
    private final ScheduleRepo scheduleRepo;
    private final ScheduleMapper scheduleMapper;

    @Override
    public ScheduleDTO insertSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleMapper.toSchedule(scheduleDTO);
        schedule.setActive(true);
        scheduleRepo.save(schedule);
        return scheduleMapper.toScheduleDTO(schedule);
    }

    @Override
    public void updateSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepo.findByName(scheduleDTO.getName())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        scheduleRepo.save(schedule);
    }

    @Override
    public void deleteSchedule(String name, Long movieId) {
        Schedule schedule = scheduleRepo.findByNameAndMovieId(name, movieId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        scheduleRepo.delete(schedule);
    }
}
