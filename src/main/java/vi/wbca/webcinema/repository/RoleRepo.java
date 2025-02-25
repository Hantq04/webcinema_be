package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
}
