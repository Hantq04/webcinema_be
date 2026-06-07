package vi.wbca.webcinema.repository.cinema;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.cinema.Cinema;

import java.util.List;
import java.util.Optional;

@Repository
public interface CinemaRepo extends JpaRepository<Cinema, Long> {
    Optional<Cinema> findByNameOfCinema(String name);
    Optional<Cinema> findByNameOfCinemaIgnoreCase(String name);
    Optional<Cinema> findByCode(String code);

    List<Cinema> findAllByOrderByIdAsc();

    List<Cinema> findAllByIsActiveTrueOrderByIdAsc();

    List<Cinema> findAllByAddressIgnoreCaseAndIsActiveTrueOrderByNameOfCinemaAsc(String address);

    List<Cinema> findAllByAddressContainingIgnoreCaseAndIsActiveTrueOrderByNameOfCinemaAsc(String keyword);
}
