package com.example.sortitems.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "activity_logs")
public class ActivityLog {

    @Id
    private String id; // Changed from Long to String

    private String action; // "ADD", "EDIT", "DELETE"
    private String itemId;
    private String itemName;
    private LocalDateTime timestamp;

    // Constructors
    public ActivityLog() {}

    public ActivityLog(String action, String itemId, String itemName) {
        this.action = action;
        this.itemId = itemId;
        this.itemName = itemName;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { // Changed return type to String
        return id;
    }

    public void setId(String id) { // Changed parameter type to String
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}