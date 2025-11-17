package vi.wbca.webcinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.user.UserStatus;
import vi.wbca.webcinema.service.userStatusService.UserStatusService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/status")
public class UserStatusController {
    private static final Logger logger = Logger.getLogger(UserStatusController.class.getName());
    private final UserStatusService userStatusService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertUserStatus(@RequestBody UserStatus userStatus) {

        logger.info("----------Web Cinema: Insert New User Status----------");

        UserStatus responseData = userStatusService.insertUserStatus(userStatus);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "User status insert successfully.", responseData)
        );
    }

    @GetMapping("/get-all-status")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public List<UserStatus> getAllStatus() {

        logger.info("----------Web Cinema: Get All User Status----------");

        return userStatusService.getAllStatus();
    }
}
