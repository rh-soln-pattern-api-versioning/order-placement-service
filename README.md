### order-placement-service


for running this in local env
create  a .env file with the following content - replace the bootstrap URL and cient ID and Secret details

```
mp.messaging.outgoing.order-event.bootstrap.servers=<URL>
mp.messaging.outgoing.order-event.topic=globex.order

mp.messaging.connector.smallrye-kafka.security.protocol=SASL_SSL
mp.messaging.connector.smallrye-kafka.sasl.mechanism=PLAIN

mp.messaging.connector.smallrye-kafka.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
  username="<client-id>" \
  password="<client-secret>" ;
```




Then run ./mvnw quarkus:dev to kickstart the app in dev mode. The payloads this /placeorder service receives will be sent to a Kafka topic called *globex.order*
