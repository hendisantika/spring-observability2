package id.my.jvm.springobservability2;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-observability2
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 21/07/26
 * Time: 06.26
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class OpenTelemetryConfig {

    /**
     * Spring Boot autoconfigures the OpenTelemetry SDK, the tracer/logger providers and the
     * OTLP exporters (see spring-boot-starter-opentelemetry and
     * spring-boot-micrometer-tracing-opentelemetry). The one thing it does not do is hand that
     * SDK to the Logback appender, so we bridge them here.
     * <p>
     * The appender buffers records emitted before installation, so wiring it on
     * ApplicationReadyEvent does not lose the startup logs.
     */
    @Bean
    ApplicationListener<ApplicationReadyEvent> openTelemetryAppenderInitializer(OpenTelemetry openTelemetry) {
        return event -> OpenTelemetryAppender.install(openTelemetry);
    }
}
