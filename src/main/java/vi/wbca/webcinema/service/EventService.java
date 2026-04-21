package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.event.EventDTO;
import vi.wbca.webcinema.model.request.EventRequest;

import java.io.IOException;
import java.util.List;

public interface EventService {
    EventDTO insertEvent(EventRequest request) throws IOException;

    void deleteEvent(String name);

    List<EventDTO> getAllEventActive();
}