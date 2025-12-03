package vi.wbca.webcinema.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.util.logging.logForm.ErrorLog;
import vi.wbca.webcinema.util.logging.LoggingUtils;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorLog errorForm = ErrorLog.builder()
                .errorLevel(authException.getClass().getSimpleName())
                .description(authException.getMessage())
                .location(authException.getStackTrace()[0].toString())
                .build();
        LoggingUtils.loggingError(authException);

        ResponseObject responseError = new ResponseObject(
                errorCode.getCode(), errorCode.getMessage(), errorForm);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseError));
        response.flushBuffer();
    }
}
