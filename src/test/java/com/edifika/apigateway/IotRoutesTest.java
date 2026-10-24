package com.edifika.apigateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class IotRoutesTest {

    private static final WireMockServer wireMockServer =
            new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());

    static {
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", wireMockServer::port);
        registry.add("services.access.url", () -> "http://localhost:" + wireMockServer.port());
        registry.add("services.telemetry.url", () -> "http://localhost:" + wireMockServer.port());
        registry.add("ACCESS_SERVICE_URL", () -> "http://localhost:" + wireMockServer.port());
        registry.add("TELEMETRY_SERVICE_URL", () -> "http://localhost:" + wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @Autowired
    private WebTestClient http;

    @Autowired
    private RouteLocator routeLocator;

    @BeforeEach
    void resetWireMock() {
        WireMock.reset();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/iot/devices",
            "/api/v1/access/me/doors",
            "/api/v1/edge/sync",
            "/api/v1/telemetry/devices"
    })
    void forwardsIotPrefixes(String path) {
        stubFor(get(urlPathEqualTo(path)).willReturn(ok("[]")));

        http.get().uri(path)
                .exchange()
                .expectStatus().isOk();

        verify(getRequestedFor(urlPathEqualTo(path)));
    }

    @Test
    void preflightIsAllowed() {
        http.options().uri("/api/v1/iot/devices")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "PUT")
                .exchange()
                .expectHeader().exists("Access-Control-Allow-Origin");
    }

    @Test
    void existingRoutesDoNotOverlap() {
        List<Route> routes = routeLocator.getRoutes().collectList().block();
        assertThat(routes).isNotNull();

        Set<String> newRouteIds = Set.of(
                "iot-access-iot",
                "iot-access-access",
                "iot-access-edge",
                "iot-telemetry"
        );

        List<String> iotPrefixes = List.of(
                "/api/v1/iot",
                "/api/v1/access",
                "/api/v1/edge",
                "/api/v1/telemetry"
        );

        for (Route route : routes) {
            if (!newRouteIds.contains(route.getId())) {
                String predicateStr = route.getPredicate().toString();
                for (String prefix : iotPrefixes) {
                    assertThat(predicateStr)
                            .as("Existing route %s must not overlap with IoT prefix %s", route.getId(), prefix)
                            .doesNotContain(prefix);
                }
            }
        }
    }
}
