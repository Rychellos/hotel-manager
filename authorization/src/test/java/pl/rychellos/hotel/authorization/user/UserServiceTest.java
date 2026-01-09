package pl.rychellos.hotel.authorization.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationExceptionFactory exceptionFactory;

    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        assertNotNull(userService);
    }

    @Test
    void updatePassword_success() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("john");
        userEntity.setPassword("oldPassword");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserDetails result = userService.updatePassword(userEntity, "newPassword");

        assertNotNull(result);
        assertEquals("newPassword", result.getPassword());

        verify(userRepository).findByUsername("john");
        verify(userRepository).save(userEntity);
    }

    @Test
    void updatePassword_userNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        when(langUtil.getMessage("error.user.notFound.byUsername", "missing"))
                .thenReturn("User with username missing not found");

        ApplicationException applicationException = new ApplicationException(
                "Resource not found",
                "User with username missing not found",
                HttpStatus.NOT_FOUND);

        when(exceptionFactory.resourceNotFound("User with username missing not found"))
                .thenReturn(applicationException);

        UserEntity principal = new UserEntity();
        principal.setUsername("missing");

        ApplicationException thrown = assertThrows(
                ApplicationException.class,
                () -> userService.updatePassword(principal, "password"));

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
        assertEquals("Resource not found", thrown.getTitle());
        assertEquals("User with username missing not found", thrown.getDetail());

        verify(userRepository).findByUsername("missing");
        verify(userRepository, never()).save(any());
    }

    @Test
    void loadUserByUsername_success() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("alice");
        userEntity.setPassword("password");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(userEntity));

        UserDetails result = userService.loadUserByUsername("alice");

        assertNotNull(result);
        assertEquals("alice", result.getUsername());

        verify(userRepository).findByUsername("alice");
    }

    @Test
    void loadUserByUsername_userNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(langUtil.getMessage("error.user.notFound.byUsername", "unknown"))
                .thenReturn("User with username unknown not found");

        UsernameNotFoundException thrown = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("unknown"));

        assertEquals("User with username unknown not found", thrown.getMessage());

        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void loadUserByUsername_nullOrEmpty() {
        when(langUtil.getMessage("error.user.username.empty"))
                .thenReturn("Username cannot be empty");

        // Test null
        UsernameNotFoundException thrownNull = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(null));
        assertEquals("Username cannot be empty", thrownNull.getMessage());

        // Test empty
        UsernameNotFoundException thrownEmpty = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(""));
        assertEquals("Username cannot be empty", thrownEmpty.getMessage());

        verify(langUtil, times(2)).getMessage("error.user.username.empty");
    }
}
