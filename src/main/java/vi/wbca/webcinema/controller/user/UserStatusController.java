package vi.wbca.webcinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.UserStatus;
import vi.wbca.webcinema.service.userStatusService.UserStatusService;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/status")
public class UserStatusController {
    private final UserStatusService userStatusService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertUserStatus(@RequestBody UserStatus userStatus) {
        UserStatus responseData = userStatusService.insertUserStatus(userStatus);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "User status insert successfully.", responseData)
        );
    }

    @GetMapping("/get-all-status")
    public List<UserStatus> getAllStatus() {
        return userStatusService.getAllStatus();
    }
}
