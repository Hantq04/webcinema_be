package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
}
