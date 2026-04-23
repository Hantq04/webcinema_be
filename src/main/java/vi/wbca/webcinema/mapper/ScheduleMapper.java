package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.dto.schedule.ScheduleDTO;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.response.ScheduleResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
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

    @Mapping(source = "schedule.id", target = "id")
    @Mapping(source = "schedule.room.name", target = "cinema")
    @Mapping(source = "schedule.room.code", target = "roomCode")
    @Mapping(source = "schedule.room.type", target = "roomType")
    @Mapping(source = "schedule.movie.name", target = "movie")
    @Mapping(source = "schedule.startAt", target = "startAt")
    @Mapping(source = "schedule.endAt", target = "endAt")
    @Mapping(source = "schedule.code", target = "code")
    @Mapping(source = "schedule.name", target = "name")
    @Mapping(source = "schedule.active", target = "isActive")
    ScheduleResponse toScheduleResponse(Schedule schedule);
}
