package org.globex.retail;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.apicurio.registry.resolver.SchemaResolverConfig;
import io.apicurio.registry.resolver.strategy.ArtifactReference;
import io.apicurio.registry.rest.client.RegistryClient;
import io.apicurio.registry.rest.client.RegistryClientFactory;
import io.apicurio.rest.client.auth.OidcAuth;
import io.apicurio.rest.client.auth.exception.AuthErrorHandler;
import io.apicurio.rest.client.spi.ApicurioHttpClient;
import io.apicurio.rest.client.spi.ApicurioHttpClientFactory;
import io.apicurio.schema.validation.json.JsonMetadata;
import io.apicurio.schema.validation.json.JsonRecord;
import io.apicurio.schema.validation.json.JsonValidationResult;
import io.apicurio.schema.validation.json.JsonValidator;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;


import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@ApplicationScoped
public class JsonSchemaValidator {

    private static final Logger log = LoggerFactory.getLogger(JsonSchemaValidator.class);
    
    
    @ConfigProperty(name = "JSON_SCHEMA")
    private String schemaFile;

    
    @ConfigProperty(name = "REGISTRY_URL") 
    private  String REGISTRY_URL;
    
    @ConfigProperty(name = "REGISTRY_GROUPID") 
    private   String groupId;
    
    @ConfigProperty(name = "REGISTRY_ARTIFACTID") 
    private   String artifactId;
    
    @ConfigProperty(name = "REGISTRY_ARTIFACTID_VERSION") 
    private   String artifactVersion;


    static private JsonValidator validator;
    static private ArtifactReference artifactReference;

    @PostConstruct
    public void loadSchema() {
        
         // Register the schema with the registry (only if it is not already registered)
        RegistryClient client = createRegistryClient(REGISTRY_URL);
        
        // Create an artifact reference pointing to the artifact we just created
        // and pass it to the JsonValidator
        artifactReference = ArtifactReference.builder()
            .groupId(groupId)
            .artifactId(artifactId).version(artifactVersion)
            .build();

        // Create the JsonValidator providing an ArtifactReference
        // this ArtifactReference will allways be used to lookup the schema in the registry when using "validateByArtifactReference"
        validator = createJsonValidator(artifactReference, REGISTRY_URL);
        log.info("Scheme fetched from Service Registry");

    }

    

    public JsonValidationResult validate(String json) {
        log.info("Validating message bean using dynamic ArtifactReference resolution");
        
       
        JsonRecord record = new JsonRecord(json, new JsonMetadata(artifactReference));

        
        JsonValidationResult recordValidationResult = validator.validate(record);
        if(recordValidationResult.success()) {
            log.info("Validation result: " + recordValidationResult + "\n");            
        } else { 
            log.error("Validation result: " + recordValidationResult + "\n");
        }
        return recordValidationResult;
        
    }
    


    /**
     * Creates the registry client
     */
    private RegistryClient createRegistryClient(String registryUrl) {
        final String tokenEndpoint = System.getenv(SchemaResolverConfig.AUTH_TOKEN_ENDPOINT);

        //Just if security values are present, then we configure them.
        if (tokenEndpoint != null) {
            final String authClient = System.getenv(SchemaResolverConfig.AUTH_CLIENT_ID);
            final String authSecret = System.getenv(SchemaResolverConfig.AUTH_CLIENT_SECRET);
            ApicurioHttpClient httpClient = ApicurioHttpClientFactory.create(tokenEndpoint, new AuthErrorHandler());
            OidcAuth auth = new OidcAuth(httpClient, authClient, authSecret);
            return RegistryClientFactory.create(registryUrl, Collections.emptyMap(), auth);
        } else {
            return RegistryClientFactory.create(registryUrl);
        }
    }

    private JsonValidator createJsonValidator(ArtifactReference artifactReference, String registryUrl) {
        Map<String, Object> props = new HashMap<>();

        // Configure Service Registry location
        props.putIfAbsent(SchemaResolverConfig.REGISTRY_URL, registryUrl);

        //Just if security values are present, then we configure them.
        // at the moment we haven't introduced auth
        configureSecurityIfPresent(props);

        // Create the json validator
        JsonValidator validator = new JsonValidator(props, Optional.ofNullable(artifactReference));
        return validator;
    }

    private void configureSecurityIfPresent(Map<String, Object> props) {
        final String tokenEndpoint = System.getenv(SchemaResolverConfig.AUTH_TOKEN_ENDPOINT);
        if (tokenEndpoint != null) {

            final String authClient = System.getenv(SchemaResolverConfig.AUTH_CLIENT_ID);
            final String authSecret = System.getenv(SchemaResolverConfig.AUTH_CLIENT_SECRET);

            props.putIfAbsent(SchemaResolverConfig.AUTH_CLIENT_SECRET, authSecret);
            props.putIfAbsent(SchemaResolverConfig.AUTH_CLIENT_ID, authClient);
            props.putIfAbsent(SchemaResolverConfig.AUTH_TOKEN_ENDPOINT, tokenEndpoint);
        }
    }
}
