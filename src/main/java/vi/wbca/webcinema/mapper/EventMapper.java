package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.model.dto.event.EventDTO;
import vi.wbca.webcinema.model.entity.event.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "active", target = "isActive")
    EventDTO toEventDTO(Event event);
}