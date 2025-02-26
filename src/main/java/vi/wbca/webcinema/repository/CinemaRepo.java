package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Cinema;

@Repository
public interface CinemaRepo extends JpaRepository<Cinema, Long> {
}
