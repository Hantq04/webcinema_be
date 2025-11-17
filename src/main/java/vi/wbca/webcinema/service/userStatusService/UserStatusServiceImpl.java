package vi.wbca.webcinema.service.userStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.user.UserStatus;
import vi.wbca.webcinema.repository.user.UserStatusRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService{
    private final UserStatusRepo userStatusRepo;

    @Override
    public UserStatus insertUserStatus(UserStatus userStatus) {
        return userStatusRepo.save(userStatus);
    }

    @Override
    public List<UserStatus> getAllStatus() {
        return userStatusRepo.findAll();
    }
}
