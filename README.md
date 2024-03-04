### order-placement-service


for running this in local env
create  a .env file with the following content - replace the bootstrap URL and cient ID and Secret details

```


export JSON_SCHEMA=order-placement-payload.json

export ORDER_PLACEMENT_API=https://webhook.site/ef4a280e-1575-4366-9cd9-159dce9e26a6
```

Then run ./mvnw quarkus:dev to kickstart the app in dev mode. The payloads this /placeorder service receives will be sent to a Kafka topic called *globex.order*


## Push image to quay

./mvnw package

docker build -f src/main/docker/Dockerfile.jvm -t  quay.io/rh_soln_pattern_api_versioning/order-placement:1.0.0 .

docker push quay.io/rh_soln_pattern_api_versioning/order-placement:1.0.0