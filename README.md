### order-placement-service


for running this in local env
create  a .env file with the following content - replace the bootstrap URL and cient ID and Secret details

```
mp.messaging.outgoing.order-event.bootstrap.servers=<URL>
mp.messaging.outgoing.order-event.topic=globex.order

mp.messaging.connector.smallrye-kafka.security.protocol=SASL_PLAINTEXT
mp.messaging.connector.smallrye-kafka.sasl.mechanism=SCRAM-SHA-512

mp.messaging.connector.smallrye-kafka.sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required \
    username="globex" \
    password="globex";

export JSON_SCHEMA=order-placement-payload.json

mp.messaging.outgoing.order-event.connector=smallrye-kafka
mp.messaging.outgoing.order-event.key.serializer=org.apache.kafka.common.serialization.StringSerializer
mp.messaging.outgoing.order-event.value.serializer=org.apache.kafka.common.serialization.StringSerializer

%dev.quarkus.kafka.devservices.enabled=false



security.protocol=SASL_PLAINTEXT
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required \
    username="globex" \
    password="globex";

```

Then run ./mvnw quarkus:dev to kickstart the app in dev mode. The payloads this /placeorder service receives will be sent to a Kafka topic called *globex.order*


## Push image to quay

./mvnw package

docker build -f src/main/docker/Dockerfile.jvm -t  quay.io/rh_soln_pattern_api_versioning/order-placement:latest .

docker push quay.io/rh_soln_pattern_api_versioning/order-placement:latest