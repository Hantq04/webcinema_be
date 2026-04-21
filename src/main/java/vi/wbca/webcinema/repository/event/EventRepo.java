package vi.wbca.webcinema.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.event.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
    Optional<Event> findByNameAndIsActiveTrue(String name);

    List<Event> findByIsActiveTrue();
}