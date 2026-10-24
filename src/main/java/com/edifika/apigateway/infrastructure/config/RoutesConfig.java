package com.edifika.apigateway.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway routes configuration.
 * Each microservice route is defined here using environment variables
 * to support local and production deployments.
 */
@Configuration
public class RoutesConfig {

    @Value("${services.iam.url}")
    private String iamServiceUrl;

    @Value("${services.residential.url}")
    private String residentialServiceUrl;

    @Value("${services.reservation.url}")
    private String reservationServiceUrl;

    @Value("${services.payment.url}")
    private String paymentServiceUrl;

    @Value("${services.communication.url}")
    private String communicationServiceUrl;

    @Value("${services.forum.url}")
    private String forumServiceUrl;

    @Value("${services.notification.url}")
    private String notificationServiceUrl;

    @Value("${services.report.url}")
    private String reportServiceUrl;

    @Value("${services.access.url}")
    private String accessServiceUrl;

    @Value("${services.telemetry.url}")
    private String telemetryServiceUrl;

    @Bean
    public RouteLocator edifikaRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // IAM Service
                .route("iam-authentication", r -> r
                        .path("/api/v1/authentication/**")
                        .uri(iamServiceUrl))
                .route("iam-roles", r -> r
                        .path("/api/v1/roles/**")
                        .uri(iamServiceUrl))
                .route("iam-users", r -> r
                        .path("/api/v1/users/**")
                        .uri(iamServiceUrl))

                // Residential Management Service
                .route("residential-all", r -> r
                        .path("/api/v1/residential/**")
                        .uri(residentialServiceUrl))

                // Reservation Service
                .route("reservation-common-areas", r -> r
                        .path("/api/v1/common-areas/**")
                        .uri(reservationServiceUrl))
                .route("reservation-reservations", r -> r
                        .path("/api/v1/reservations/**")
                        .uri(reservationServiceUrl))

                // Payment Service
                .route("payment-payments", r -> r
                        .path("/api/v1/payments/**")
                        .uri(paymentServiceUrl))

                // Communication Service
                .route("communication-announcements", r -> r
                        .path("/api/v1/announcements/**")
                        .uri(communicationServiceUrl))

                // Forum Service
                .route("forum-posts", r -> r
                        .path("/api/v1/posts/**")
                        .uri(forumServiceUrl))

                // Notification service
                .route("notification-service", r -> r
                        .path("/api/v1/notifications/**")
                        .uri(notificationServiceUrl))
                
                .route("notification-device", r -> r
                        .path("/api/v1/device-tokens/**")
                        .uri(notificationServiceUrl))

                // Report Service
                .route("reports-service", r -> r
                        .path("/api/v1/reports/**")
                        .uri(reportServiceUrl))

                // IoT Access Management Service
                .route("iot-access-iot", r -> r
                        .path("/api/v1/iot/**")
                        .uri(accessServiceUrl))
                .route("iot-access-access", r -> r
                        .path("/api/v1/access/**")
                        .uri(accessServiceUrl))
                .route("iot-access-edge", r -> r
                        .path("/api/v1/edge/**")
                        .uri(accessServiceUrl))

                // IoT Telemetry Service
                .route("iot-telemetry", r -> r
                        .path("/api/v1/telemetry/**")
                        .uri(telemetryServiceUrl))
                .build();
    }
}
