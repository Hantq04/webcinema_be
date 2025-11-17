package vi.wbca.webcinema.repository.cinema;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.cinema.Cinema;

import java.util.Optional;

@Repository
public interface CinemaRepo extends JpaRepository<Cinema, Long> {
    Optional<Cinema> findByNameOfCinema(String name);
    Optional<Cinema> findByCode(String code);
}
