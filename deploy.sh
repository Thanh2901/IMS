#!/bin/bash

# stop if any script fails
set -e

BACKEND_DIR="./backend"
SERVICES=("api_gateway" "auth_service" "camera_service" "eureka_server" "infrastructure_service" "notification_service" "report_service" "user_service")

echo "Building JAR files for all backend services..."

# build jar files
for SERVICE in "${SERVICES[@]}"; do
  SERVICE_PATH="$BACKEND_DIR/$SERVICE"
  echo "Building $SERVICE_PATH..."
  (cd "$SERVICE_PATH" && mvn clean install -DskipTests -DskipSpotless)
  echo "Built $SERVICE_PATH successfully!"
done

echo "All services built successfully!"

echo "Starting Docker Compose for the application..."

# run everything
docker compose up --build

echo "Docker Compose is running!"
