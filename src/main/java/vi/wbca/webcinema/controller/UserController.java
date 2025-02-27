package vi.wbca.webcinema.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.UserDTO;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.service.userService.UserService;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertUser(@Valid @RequestBody UserDTO request) {
        User responseData = userService.insertUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "User insert successfully", responseData)
        );
    }

    @GetMapping("/get-all")
    public List<User> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/find-by-user-name")
    public ResponseEntity<ResponseObject> findByUserName(@Valid @RequestParam String userName) {
        User responseData = userService.findByUserName(userName);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Find user successfully", responseData)
        );
    }
}
