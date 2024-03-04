package org.globex.retail.service;


import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





@ApplicationScoped
public class JsonSchemaValidator {

    private static final Logger log = LoggerFactory.getLogger(JsonSchemaValidator.class);
        
    
    @ConfigProperty(name = "JSON_SCHEMA")
    private String schemaFile;

   
    
    public void validate(String json)  {
        try (InputStream inputStream = getClass().getResourceAsStream("/org/globex/retail/json-schema/"+schemaFile)) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(new JSONObject(json)); // throws a ValidationException if this object is invalid
        } catch (IOException e) {
            // prints #/rectangle/a: -5.0 is not higher or equal to 0
            log.error(e.getMessage());
            
        }
    }
    



}
