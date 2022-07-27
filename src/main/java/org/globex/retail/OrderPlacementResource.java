package org.globex.retail;

import java.util.Random;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.smallrye.mutiny.Uni;
import org.everit.json.schema.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/placeorder")
public class OrderPlacementResource {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacementResource.class);

    @Inject
    JsonSchemaValidator validator;

    @Inject
    KafkaService kafkaService;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> placeOrder(String payload) {
    
        // a random  number sequence of 6 character  - fake order id
        String order_id = String.format("%06d", new Random().nextInt(999999));
        
        String message = "{\"status\": \"CONFIRMED\", \"order_id\": \""+ order_id + "\"}";


        return Uni.createFrom().item(() -> payload)
                //.onItem().invoke(p -> validator.validate(p))
                
                .onItem().invoke(p -> kafkaService.emit(p))
                .onItem().transform(p -> Response.status(200).entity(message).type(MediaType.APPLICATION_JSON).build())
                .onFailure().recoverWithItem(throwable -> {
                    if (throwable instanceof ValidationException) {
                        log.error("Exception validating payload", throwable);
                        return Response.status(400, "Payload validation error").build();
                    } else {
                        log.error("Exception when processing payload", throwable);
                        return Response.status(500, "Processing error").build();
                    }
                });
    }

}
