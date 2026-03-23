package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.user.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByCode(String code);
}
