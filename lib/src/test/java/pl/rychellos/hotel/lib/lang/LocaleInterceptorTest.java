package pl.rychellos.hotel.lib.lang;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocaleInterceptorTest {

    @InjectMocks
    private LocaleInterceptor interceptor;

    @Mock
    private LocaleHolder localeHolder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AcceptHeaderLocaleResolver localeResolver;

    @Test
    void preHandle_ShouldSetLocaleInHolder_WhenValidResolverExists() {
        /// Given
        Locale userLocale = Locale.FRENCH;
        // Mocking RequestContextUtils behavior
        when(request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE))
            .thenReturn(localeResolver);
        when(localeResolver.resolveLocale(request)).thenReturn(userLocale);

        /// When
        boolean result = interceptor.preHandle(request, response, new Object());

        /// Then
        assertTrue(result);
        verify(localeHolder).setCurrentLocale(userLocale);
    }

    @Test
    void preHandle_ShouldThrowException_WhenNoResolverFound() {
        /// Given
        when(request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE)).thenReturn(null);

        /// When & Then
        assertThrows(IllegalStateException.class, () ->
            interceptor.preHandle(request, response, new Object())
        );
    }
}