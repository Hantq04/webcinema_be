package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Cinema;

import java.util.Optional;

@Repository
public interface CinemaRepo extends JpaRepository<Cinema, Long> {
    Optional<Cinema> findByNameOfCinema(String name);
    Optional<Cinema> findByCode(String code);
}
