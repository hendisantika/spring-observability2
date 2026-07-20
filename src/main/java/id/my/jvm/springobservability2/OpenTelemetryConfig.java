package id.my.jvm.springobservability2;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.SdkLoggerProviderBuilder;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.semconv.ServiceAttributes;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
    @Bean
    OpenTelemetry openTelemetry(SdkLoggerProvider sdkLoggerProvider, SdkTracerProvider sdkTracerProvider, ContextPropagators contextPropagators) {
        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
                .setLoggerProvider(sdkLoggerProvider)
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(contextPropagators)
                .build();
        OpenTelemetryAppender.install(openTelemetrySdk);
        return openTelemetrySdk;
    }

    @Bean
    SdkLoggerProvider otelSdkLoggerProvider(Environment environment, ObjectProvider<LogRecordProcessor> logRecordProcessors) {
        String applicationName = environment.getProperty("spring.application.name", "application");
        Resource springResource = Resource.create(Attributes.of(ServiceAttributes.SERVICE_NAME, applicationName));
        SdkLoggerProviderBuilder builder = SdkLoggerProvider.builder()
                .setResource(Resource.getDefault().merge(springResource));
        logRecordProcessors.orderedStream().forEach(builder::addLogRecordProcessor);
        return builder.build();
    }

    @Bean
    LogRecordProcessor otelLogRecordProcessor() {
        return BatchLogRecordProcessor
                .builder(
                        OtlpGrpcLogRecordExporter.builder()
                                .setEndpoint("http://localhost:4317")
                                .build())
                .build();
    }
}
