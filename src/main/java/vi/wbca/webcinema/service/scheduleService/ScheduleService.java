package vi.wbca.webcinema.service.scheduleService;

import vi.wbca.webcinema.dto.ScheduleDTO;

public interface ScheduleService {
    ScheduleDTO insertSchedule(ScheduleDTO scheduleDTO);

    void updateSchedule(ScheduleDTO scheduleDTO);

    void deleteSchedule(String name, Long movieId);
}
