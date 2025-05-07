#!/bin/bash
set -e # exit on error

services=("api_gateway" "auth_service" "camera_service" "eureka_server" "infrastructure_service" "notification_service" "report_service" "user_service")

# down containers on exiting
cleanup() {
  echo "Stopping all services..."
  docker compose down
}
trap cleanup EXIT

docker compose down
docker compose up -d # detached

pids=()

for service in "${services[@]}"; do
  service_path="./$service"
  echo "Running $service"
  chmod +x "$service_path/mvnw"
  (cd "$service_path" && ./mvnw spring-boot:run -DskipSpotless=true -DskipTests=true) &
  pids+=($!) # run in background and store pids
done

# wait for all services to finish
for pid in "${pids[@]}"; do
  wait "$pid"
done

echo "All services started successfully"
