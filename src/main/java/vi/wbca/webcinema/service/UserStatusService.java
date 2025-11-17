package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.user.UserStatus;

import java.util.List;

public interface UserStatusService {
    UserStatus insertUserStatus(UserStatus userStatus);

    List<UserStatus> getAllStatus();
}
