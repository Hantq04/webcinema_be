package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.EventMapper;
import vi.wbca.webcinema.model.dto.event.EventDTO;
import vi.wbca.webcinema.model.entity.event.Event;
import vi.wbca.webcinema.repository.event.EventRepo;
import vi.wbca.webcinema.service.EventService;
import vi.wbca.webcinema.model.request.EventRequest;
import vi.wbca.webcinema.util.ImageUtils;

import java.io.IOException;
import java.util.List;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final EventMapper eventMapper;

    @Override
    public EventDTO insertEvent(EventRequest request) throws IOException {
        String imageUrl = ImageUtils.saveImage(request.getFile());

        Event event = new Event();
        event.setName(request.getName());
        event.setImageUrl(imageUrl);
        event.setActive(true);
        event = eventRepo.save(event);
        return eventMapper.toEventDTO(event);
    }

    @Override
    public void deleteEvent(String name) {
        Event event = eventRepo.findByNameAndIsActiveTrue(name)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        event.setActive(false);
        eventRepo.save(event);
    }

    @Override
    public List<EventDTO> getAllEventActive() {
        return eventRepo.findByIsActiveTrue().stream()
                .sorted(Comparator.comparing(Event::getId).reversed())
                .map(eventMapper::toEventDTO)
                .toList();
    }
}