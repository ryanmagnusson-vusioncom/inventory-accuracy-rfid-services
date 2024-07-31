mvn clean compile package install

docker build -t vusion-rfid-services:latest .

docker run -p 80:8080 vusion-rfid-services:latest
