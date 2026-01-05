package pl.rychellos.hotel.lib.lang;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Slf4j
@Configuration
public class LangConfig implements WebMvcConfigurer {
    final LocaleInterceptor localeInterceptor;

    public LangConfig(LocaleInterceptor localeInterceptor) {
        this.localeInterceptor = localeInterceptor;
    }

    @Bean
    public AcceptHeaderLocaleResolver acceptHeaderLocaleResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);

        return localeResolver;
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public LocaleHolder localeHolder() {
        return new LocaleHolder();
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(localeInterceptor);
    }

    @Bean
    public MessageSource messageSource() throws IOException {
        log.info("Started scanning for messages-*.properties");
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = resolver.getResources("classpath*:messages-*");
        log.info("Found {} messages files", resources.length);
        for (Resource resource : resources) {
            log.info(" - {}", resource.getFilename().substring(0, resource.getFilename().lastIndexOf(".")));
        }

        Set<String> baseNames = getBaseNames(resources);

        messageSource.setBasenames(baseNames.toArray(new String[0]));
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }

    private static @NonNull Set<String> getBaseNames(Resource[] resources) {
        Set<String> baseNames = new HashSet<>();

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename != null) {
                String nameWithoutExtension = filename.replace(".properties", "");

                String baseNameOnly = nameWithoutExtension.split("_[a-z]{2}")[0];

                baseNames.add("classpath:" + baseNameOnly);
            }
        }

        // default override
        baseNames.add("classpath:messages");

        return baseNames;
    }
}
