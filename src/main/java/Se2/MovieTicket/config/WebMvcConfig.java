package Se2.MovieTicket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LocaleConfig localeConfig;

    public WebMvcConfig(LocaleConfig localeConfig) {
        this.localeConfig = localeConfig;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeConfig.localeChangeInterceptor());
    }
}