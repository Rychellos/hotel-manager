package pl.rychellos.hotel.lib.lang;

import jakarta.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class LangUtil {
    final MessageSource messageSource;

    @Resource(name = "localeHolder")
    LocaleHolder localeHolder;

    public LangUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code, String... args) {
        return messageSource.getMessage(code, args, localeHolder.getCurrentLocale());
    }
}
