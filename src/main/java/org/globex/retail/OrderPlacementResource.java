package org.globex.retail;

import java.util.Random;



import io.apicurio.schema.validation.json.JsonValidationResult;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.validator.ValidatorException;
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
        JsonValidationResult jsonValidationResult = validator.validate(payload);
        
        if (!jsonValidationResult.success()) {
            String errorMessage = "{\"status\": \"ERROR\", \"message\": \""+ jsonValidationResult.getValidationErrors().toString() + "\"}";
            return Uni.createFrom().item(() -> payload)
            .onItem().transform(p -> Response.status(400).entity(errorMessage).build());
                
        } else  {
            return Uni.createFrom().item(() -> payload)
                .onItem().invoke(p -> kafkaService.emit(p))
                .onItem().transform(p -> Response.status(200).entity(message).type(MediaType.APPLICATION_JSON).build())
                .onFailure().recoverWithItem(throwable -> {                    
                        log.error("Exception when processing payload", throwable);
                        return Response.status(500, "Processing error").build();                    
                });}
    }

}
