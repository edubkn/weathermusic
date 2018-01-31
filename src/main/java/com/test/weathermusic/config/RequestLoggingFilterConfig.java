package com.test.weathermusic.config;

import com.test.weathermusic.config.filter.CustomRequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Eduardo on 30/01/2018.
 */
@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    public CustomRequestLoggingFilter logFilter() {
        CustomRequestLoggingFilter filter = new CustomRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        return filter;
    }
}
