package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.schedule.ScheduleDTO;
import vi.wbca.webcinema.model.movie.Schedule;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "room", ignore = true)
    Schedule toSchedule(ScheduleDTO scheduleDTO);

    @Mapping(target = "movieName", ignore = true)
    @Mapping(target = "roomName", ignore = true)
    @Mapping(target = "roomCode", ignore = true)
    ScheduleDTO toScheduleDTO(Schedule schedule);
}
