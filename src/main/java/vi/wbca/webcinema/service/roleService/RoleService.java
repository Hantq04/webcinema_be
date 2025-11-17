package vi.wbca.webcinema.service.roleService;

import vi.wbca.webcinema.model.user.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByCode(String code);
}
