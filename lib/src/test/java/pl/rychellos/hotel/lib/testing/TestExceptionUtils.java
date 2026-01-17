package pl.rychellos.hotel.lib.testing;

import static org.mockito.ArgumentMatchers.anyString;

import org.mockito.Mockito;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

public class TestExceptionUtils {
    public static ApplicationExceptionFactory createFakeFactory() {
        // Create a LangUtil that just returns the key as the message
        LangUtil mockLang = Mockito.mock(LangUtil.class);
        Mockito.lenient().when(mockLang.getMessage(anyString())).thenAnswer(i -> i.getArgument(0));

        return new ApplicationExceptionFactory(mockLang);
    }
}
