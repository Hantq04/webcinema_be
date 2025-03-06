package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.ScheduleDTO;
import vi.wbca.webcinema.model.Schedule;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    Schedule toSchedule(ScheduleDTO scheduleDTO);

    ScheduleDTO toScheduleDTO(Schedule schedule);
}
