package org.globex.retail.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/")
@RegisterRestClient(configKey="order-placement-api")
public interface OrderService {

    @PUT
    @Path("/")
    void placeOrder(String order);

}
