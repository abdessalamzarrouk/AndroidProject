package com.example.myapplication.model;

public class Comment {
    private String userEmail;
    private String commentText;
    private String offerId;
    private long timestamp;

    public Comment() {
        // Default constructor required for Firestore
    }

    public Comment(String userEmail, String commentText, String offerId, long timestamp) {
        this.userEmail = userEmail;
        this.commentText = commentText;
        this.offerId = offerId;
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
