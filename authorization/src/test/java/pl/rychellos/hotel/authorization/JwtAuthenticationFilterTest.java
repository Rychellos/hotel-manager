package pl.rychellos.hotel.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.rychellos.hotel.authorization.service.JwtService;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldAuthenticate_WhenValidTokenProvided() throws Exception {
        /// Given
        String jwt = "valid.token.string";
        String username = "test@hotel.com";
        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
        when(jwtService.isTokenValid(jwt, user)).thenReturn(true);

        /// When
        filter.doFilterInternal(request, response, filterChain);

        /// Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldSkip_WhenNoAuthHeaderPresent() throws Exception {
        /// Given
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        /// When
        filter.doFilterInternal(request, response, filterChain);

        /// Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_ShouldHandleApplicationException_ByCallingResolver() throws Exception {
        /// Given
        String jwt = "invalid.token";
        ApplicationException ex = mock(ApplicationException.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenThrow(ex);
        when(response.isCommitted()).thenReturn(false);

        /// When
        filter.doFilterInternal(request, response, filterChain);

        /// Then
        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), isNull(), eq(ex));
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldNotFilter_ShouldReturnTrue_ForAuthEndpoints() {
        /// Given
        when(request.getRequestURI()).thenReturn("/api/v1/auth/login");

        /// When
        boolean result = filter.shouldNotFilter(request);

        /// Then
        assertTrue(result);
    }

    @Test
    void shouldNotFilter_ShouldReturnTrue_ForNonApiPaths() {
        /// Given
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        /// When
        boolean result = filter.shouldNotFilter(request);

        /// Then
        assertTrue(result);
    }
}