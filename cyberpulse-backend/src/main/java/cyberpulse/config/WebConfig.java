package cyberpulse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class WebConfig {

    public PageableHandlerMethodArgumentResolverCustomizer
    pageableCustomizer() {

        return resolver -> {

            resolver.setMaxPageSize(100);

            resolver.setOneIndexedParameters(false);

            resolver.setPageParameterName("page");

            resolver.setSizeParameterName("size");
        };
    }
}