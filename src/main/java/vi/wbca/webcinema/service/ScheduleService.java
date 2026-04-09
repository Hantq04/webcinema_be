package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.schedule.ScheduleDTO;
import vi.wbca.webcinema.model.response.ScheduleResponse;
import vi.wbca.webcinema.model.response.ScheduleGroupByDateResponse;

import java.util.List;

public interface ScheduleService {
    ScheduleDTO insertSchedule(ScheduleDTO scheduleDTO);

    void updateSchedule(ScheduleDTO scheduleDTO);

    void deleteSchedule(String name, Long movieId);

    void deactivateExpiredSchedule();

    List<ScheduleResponse> getAllSchedule();

    List<ScheduleGroupByDateResponse> getSchedulesByMovieGroupedByDate(Long movieId);
}
