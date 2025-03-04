package vi.wbca.webcinema.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.enums.TokenStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.AccessToken;
import vi.wbca.webcinema.service.accessTokenService.AccessTokenService;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private static final Logger logger = Logger.getLogger(LogoutService.class.getName());
    private final AccessTokenService accessTokenService;


    @SneakyThrows
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        logger.info("----------Web Cinema: Logout----------");

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.AUTH_TOKEN_EXCEPTION);
        }
        String jwtToken = authHeader.substring(7);

        AccessToken storedToken = accessTokenService.findByAccessToken(jwtToken);

        storedToken.setTokenStatus(TokenStatus.REVOKED);
        accessTokenService.save(storedToken);

        ResponseEntity<ResponseObject> logoutResponse = ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Logout successfully.", "")
        );
        response.setStatus(logoutResponse.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(logoutResponse.getBody()));
        response.flushBuffer();
    }
}
