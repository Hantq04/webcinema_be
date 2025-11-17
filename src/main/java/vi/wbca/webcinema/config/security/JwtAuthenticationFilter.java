package vi.wbca.webcinema.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import vi.wbca.webcinema.enums.TokenStatus;
import vi.wbca.webcinema.model.token.AccessToken;
import vi.wbca.webcinema.service.accessTokenService.AccessTokenService;
import vi.wbca.webcinema.util.jwt.JwtTokenProvider;

import java.io.IOException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    HandlerExceptionResolver exceptionResolver;
    JwtTokenProvider jwtTokenProvider;
    UserDetailsService userDetailsService;
    AccessTokenService accessTokenService;

    public JwtAuthenticationFilter(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver,
            JwtTokenProvider jwtTokenProvider,
            UserDetailsService userDetailsService,
            AccessTokenService accessTokenService) {
        this.exceptionResolver = exceptionResolver;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.accessTokenService = accessTokenService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authToken = request.getHeader("Authorization");
        try {
            if (authToken == null || !authToken.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = authToken.substring(7);
            String userName = jwtTokenProvider.extractUserName(jwt);

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                AccessToken accessToken = accessTokenService.findByAccessToken(jwt);

                if (jwtTokenProvider.isTokenValid(jwt, userDetails)
                        && accessToken.getTokenStatus().equals(TokenStatus.ACTIVE)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | SignatureException | UsernameNotFoundException exception) {
            if (exception instanceof ExpiredJwtException) setExpired(authToken);
            exceptionResolver.resolveException(request, response, null, exception);
        }
    }

    public void setExpired(String authToken) {
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            return;
        }
        String jwt = authToken.substring(7);
        AccessToken accessToken = accessTokenService.findByAccessToken(jwt);

        if (accessToken != null) {
            accessToken.setTokenStatus(TokenStatus.EXPIRED);
            accessTokenService.save(accessToken);
        }
    }
}
