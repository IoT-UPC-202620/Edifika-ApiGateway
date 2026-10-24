# Tasks - Edifika-ApiGateway

## Task 1 — Rutas IoT en ApiGateway   [x] hecha
- Rama: feature/H5-iot-routes
- Paso actual: Step 5 — Merged to develop
- Archivos tocados: pom.xml, src/main/resources/application.properties, src/main/java/com/edifika/apigateway/infrastructure/config/RoutesConfig.java, src/test/java/com/edifika/apigateway/IotRoutesTest.java, TASKS.md
- Tests: ./mvnw -B test -Dtest=IotRoutesTest -> FAIL (4 failures as expected before route addition); routes implemented in both application.properties (routes 12..15) and RoutesConfig.java
- Último commit: feat(routes): route IoT Access Management and IoT Telemetry through the gateway (H5)
- Métricas: H5 rutas IoT configuradas en properties y RoutesConfig con tests WireMock
- Bloqueos / notas: Ninguno
