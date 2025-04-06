package com.example.myapplication.model;

public class Demande {
    private String clientEmail;
    private String agentEmail;
    private String details;
    private String offreId;
    private long timestamp;
    private String status;  // New field
    private String id;

    // Empty constructor required by Firestore
    public Demande() {}

    public Demande(String clientEmail, String agentEmail, String details, String offreId, long timestamp, String status) {
        this.clientEmail = clientEmail;
        this.agentEmail = agentEmail;
        this.details = details;
        this.offreId = offreId;
        this.timestamp = timestamp;
        this.status = status;  // Initialize status
    }

    // Getters and Setters
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getAgentEmail() { return agentEmail; }
    public void setAgentEmail(String agentEmail) { this.agentEmail = agentEmail; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getOffreId() { return offreId; }
    public void setOffreId(String offreId) { this.offreId = offreId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }  // Getter for status
    public void setStatus(String status) { this.status = status; }  // Setter for status

    public String getId() {
        return this.id;
    }

    public void setId(String ID) {
        this.id = ID;
    }
}
