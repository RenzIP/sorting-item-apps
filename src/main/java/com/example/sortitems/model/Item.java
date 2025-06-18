package com.example.sortitems.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "items")
public class Item {
    @Id
    private ObjectId id;
    private String name;
    private int quantity;
    private String imageUrl;
    private String category;
    private boolean deleted = false;

    public Item() {
    }

    public Item(String name, int quantity, String imageUrl, String category) {
        this.name = name;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.category = category;

    }

    public ObjectId getId() {
        return id;
    }

    @JsonProperty("id")
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }

    public String getImageUrl() { // Getter untuk imageUrl
        return imageUrl;
    }

    public String getCategory() { // Getter untuk category
        return category;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setImageUrl(String imageUrl) { // Setter untuk imageUrl
        this.imageUrl = imageUrl;
    }

    public void setCategory(String category) { // Setter untuk category
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Item{" +
               "id=" + (id != null ? id.toHexString() : "null") +
               ", name='" + name + '\'' +
               ", quantity=" + quantity +
               ", imageUrl='" + imageUrl + '\'' +
               ", category='" + category + '\'' +
               '}';
    }
}