package pl.rychellos.hotel.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.rychellos.hotel.authorization.service.JwtService;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private void processToken(HttpServletRequest request) throws ApplicationException {
        log.info("Started processing access token");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String jwt;
        String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Invalid data provided as token");
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (
            userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null
        ) {
            log.info("Began authenticating user");

            UserEntity userDetails = (UserEntity) this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("Provided token is valid");

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.info("Couldn't validate token validated successfully");
            }
        } else if (userEmail == null) {
            log.info("User email is null");
        } else {
            log.info("User already authenticated");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (!path.startsWith("/api/")) {
            return true;
        }

        return path.startsWith("/api/v1/auth/login");
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) {
        try {
            processToken(request);
            filterChain.doFilter(request, response);
        } catch (ApplicationException applicationException) {
            log.info("Failed to process a token: {}", applicationException.getDetail());

            if (!response.isCommitted()) {
                handlerExceptionResolver.resolveException(request, response, null, applicationException);
            }
        } catch (Exception exception) {
            log.info("Failed to process a token", exception);

            if (!response.isCommitted()) {
                handlerExceptionResolver.resolveException(request, response, null, exception);
            }
        }
    }
}
