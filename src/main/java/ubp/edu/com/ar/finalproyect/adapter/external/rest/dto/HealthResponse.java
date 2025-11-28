package ubp.edu.com.ar.finalproyect.adapter.external.rest.dto;

/**
 * DTO for health check response from provider
 * Matches provider API contract from specs
 */
public class HealthResponse {

    private String status;   // "OK" or "KO"
    private String message;  // Optional message with details

    public HealthResponse() {
    }

    public HealthResponse(String status) {
        this.status = status;
    }

    public HealthResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isHealthy() {
        return "OK".equalsIgnoreCase(status);
    }
}
