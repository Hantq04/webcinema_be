package vi.wbca.webcinema.service.userStatusService;

import vi.wbca.webcinema.model.UserStatus;

import java.util.List;

public interface UserStatusService {
    UserStatus insertUserStatus(UserStatus userStatus);

    List<UserStatus> getAllStatus();
}
