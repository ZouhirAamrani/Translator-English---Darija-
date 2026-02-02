package com.translator.resource;

import com.translator.model.TranslationRequest;
import com.translator.model.TranslationResponse;
import com.translator.service.GeminiAPIService;
import com.translator.service.LLMService;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/translator")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TranslatorResource {
    
    private final LLMService llmService;
    
    @Context
    private SecurityContext securityContext;
    
    public TranslatorResource() {
        this.llmService = new GeminiAPIService();
    }
    
    /**
     * Translates English text to Moroccan Darija
     * POST /api/translator/translate
     * Requires authentication (USER role)
     */
    @POST
    @Path("/translate")
    @RolesAllowed({"USER", "ADMIN"})
    public Response translate(TranslationRequest request) {
        try {
            // Log authenticated user
            String username = securityContext.getUserPrincipal().getName();
            System.out.println("Translation request from user: " + username);
            
            // Validate request
            if (request == null || request.getText() == null || request.getText().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Text to translate is required"))
                    .build();
            }
            
            // Perform translation
            String translatedText = llmService.translate(request.getText());
            
            // Build response
            TranslationResponse response = new TranslationResponse(
                request.getText(),
                translatedText
            );
            
            return Response.ok(response).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
                
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Translation service error: " + e.getMessage()))
                .build();
        }
    }
    
    /**
     * Health check endpoint
     * GET /api/translator/health
     * No authentication required
     */
    @GET
    @Path("/health")
    @PermitAll
    public Response health() {
        return Response.ok()
            .entity(new HealthResponse("Translation service is running"))
            .build();
    }
    
    /**
     * Get current user info (for testing)
     * GET /api/translator/me
     * Requires authentication
     */
    @GET
    @Path("/me")
    @RolesAllowed({"USER", "ADMIN"})
    public Response getCurrentUser() {
        String username = securityContext.getUserPrincipal().getName();
        boolean isAdmin = securityContext.isUserInRole("ADMIN");
        
        return Response.ok()
            .entity(new UserInfoResponse(username, isAdmin))
            .build();
    }
    
    // Helper classes
    public static class ErrorResponse {
        private String error;
        private long timestamp;
        
        public ErrorResponse() {
        }
        
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
    
    public static class HealthResponse {
        private String status;
        private long timestamp;
        
        public HealthResponse() {
        }
        
        public HealthResponse(String status) {
            this.status = status;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
    
    public static class UserInfoResponse {
        private String username;
        private boolean admin;
        private long timestamp;
        
        public UserInfoResponse() {
        }
        
        public UserInfoResponse(String username, boolean admin) {
            this.username = username;
            this.admin = admin;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public boolean isAdmin() {
            return admin;
        }
        
        public void setAdmin(boolean admin) {
            this.admin = admin;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}