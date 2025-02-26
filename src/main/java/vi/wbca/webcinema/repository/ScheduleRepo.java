package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Schedule;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
}
