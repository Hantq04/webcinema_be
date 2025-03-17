package vi.wbca.webcinema.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vi.wbca.webcinema.util.Constants;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
    private static final Logger logger = Logger.getLogger(TestController.class.getName());

    @GetMapping("/all")
    public String allAccess() {
        logger.info("----------All Role----------");
        return "Public Content";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public String userAccess() {
        logger.info("----------User Role----------");
        return "User Content";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public String adminAccess() {
        logger.info("----------Admin Role----------");
        return "Admin Board";
    }
}
