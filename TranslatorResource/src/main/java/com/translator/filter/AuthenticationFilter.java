package com.translator.filter;

import com.translator.model.User;
import com.translator.security.UserRepository;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final String REALM = "Darija Translator API";
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Skip authentication for health endpoint
        String path = requestContext.getUriInfo().getPath();
        if (path.endsWith("/health")) {
            return;
        }
        
        // Get Authorization header
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        // Check if Authorization header is present
        if (authorizationHeader == null || !authorizationHeader.startsWith(AUTHENTICATION_SCHEME + " ")) {
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }
        
        // Extract and decode credentials
        String base64Credentials = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        String credentials;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            credentials = new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            abortWithUnauthorized(requestContext, "Invalid Base64 encoding");
            return;
        }
        
        // Split username and password
        String[] values = credentials.split(":", 2);
        if (values.length != 2) {
            abortWithUnauthorized(requestContext, "Invalid credentials format");
            return;
        }
        
        String username = values[0];
        String password = values[1];
        
        // Authenticate user
        User user = UserRepository.getInstance().authenticate(username, password);
        
        if (user == null) {
            abortWithUnauthorized(requestContext, "Invalid username or password");
            return;
        }
        
        // Set security context
        final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> user.getUsername();
            }
            
            @Override
            public boolean isUserInRole(String role) {
                return user.hasRole(role);
            }
            
            @Override
            public boolean isSecure() {
                return currentSecurityContext.isSecure();
            }
            
            @Override
            public String getAuthenticationScheme() {
                return AUTHENTICATION_SCHEME;
            }
        });
        
        System.out.println("User authenticated: " + username);
    }
    
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, 
                    AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                .entity(new ErrorResponse("Unauthorized: " + message))
                .build()
        );
    }
    
    // Error response class
    public static class ErrorResponse {
        private String error;
        private long timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}