@echo off

start "ORDER SERVICE" cmd /k "cd /d E:\KafkaKW\order-service && mvnw.cmd spring-boot:run"

start "PAYMENT SERVICE" cmd /k "cd /d E:\KafkaKW\payment-service && mvnw.cmd spring-boot:run"

start "DELIVERY SERVICE" cmd /k "cd /d E:\KafkaKW\delivery-service && mvnw.cmd spring-boot:run"

start "NOTIFICATION SERVICE" cmd /k "cd /d E:\KafkaKW\notification-service && mvnw.cmd spring-boot:run"

echo All 4 services are starting...