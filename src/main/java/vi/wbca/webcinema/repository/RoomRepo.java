package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Room;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
}
