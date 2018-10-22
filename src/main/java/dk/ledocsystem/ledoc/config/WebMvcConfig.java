package dk.ledocsystem.ledoc.config;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer, AsyncConfigurer {

    private final AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler;

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableHandlerMethodArgumentResolverCustomizer() {
        return (resolver) -> resolver.setFallbackPageable(Pageable.unpaged());
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setQueueCapacity(50);
        //taskExecutor.setAllowCoreThreadTimeOut();
        taskExecutor.setKeepAliveSeconds(120);
        taskExecutor.initialize();
        configurer.setTaskExecutor(taskExecutor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return asyncUncaughtExceptionHandler;
    }
}
