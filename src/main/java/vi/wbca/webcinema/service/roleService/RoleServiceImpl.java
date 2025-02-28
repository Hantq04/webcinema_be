package vi.wbca.webcinema.service.roleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.Role;
import vi.wbca.webcinema.repository.RoleRepo;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepo roleRepo;

    @Override
    public Optional<Role> findByCode(String code) {
        return roleRepo.findByCode(code);
    }
}
