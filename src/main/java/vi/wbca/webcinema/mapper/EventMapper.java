package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.dto.event.EventDTO;
import vi.wbca.webcinema.model.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "active", target = "isActive")
    EventDTO toEventDTO(Event event);
}