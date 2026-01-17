package pl.rychellos.hotel.lib.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
class LangUtilTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private LocaleHolder localeHolder;

    private LangUtil langUtil;

    @BeforeEach
    void setUp() {
        langUtil = new LangUtil(messageSource);
        // Manually inject the mock since we aren't using a full Spring Context
        langUtil.localeHolder = localeHolder;
    }

    @Test
    void getMessage_ShouldDelegateToMessageSourceWithCorrectLocale() {
        /// Given
        String code = "error.test";
        String[] args = {"arg1"};
        Locale locale = Locale.GERMAN;
        String expected = "Test Nachricht";

        when(localeHolder.getCurrentLocale()).thenReturn(locale);
        when(messageSource.getMessage(code, args, locale)).thenReturn(expected);

        /// When
        String result = langUtil.getMessage(code, args);

        /// Then
        assertEquals(expected, result);
        verify(messageSource).getMessage(code, args, locale);
    }
}
