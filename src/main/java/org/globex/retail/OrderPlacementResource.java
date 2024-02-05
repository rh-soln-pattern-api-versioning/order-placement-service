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
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.everit.json.schema.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/placeorder")
public class OrderPlacementResource {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacementResource.class);

    @Inject
    JsonSchemaValidator validator;

    @RestClient
    OrderService orderService;

    public void emit(String payload) {
        orderService.placeOrder(payload);
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response  placeOrder(String payload) {
    
        // a random  number sequence of 6 character  - fake order id
        String order_id = String.format("%06d", new Random().nextInt(999999));
        
        String message = "{\"status\": \"CONFIRMED\", \"order_id\": \""+ order_id + "\"}";
        
        boolean isJsonValid = true;
        String validationErrors = null;
        
        try{ 
            validator.validate(payload);
        }  catch (ValidationException e) {
            
            // prints #/rectangle/a: -5.0 is not higher or equal to 0
            System.out.println("ValidationException" + e.getMessage());
            validationErrors = e.getAllMessages().toString();
            isJsonValid = false;
        }
        
        System.out.println("ValidationException: isJsonValid:: " + isJsonValid + " | validationErrors:: " + validationErrors);
        if (!isJsonValid) {
            String errorMessage = "{\"status\": \"ERROR\", \"message\": \""+ validationErrors + "\"}";
            return Response.status(400).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
                
        } else  {
            orderService.placeOrder(payload);
            return Response.status(200).entity(message).type(MediaType.APPLICATION_JSON).build();
        }

    }
}
