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

    public Item() {
    }

    public Item(String name, int quantity, String imageUrl) {
        this.name = name;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
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

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setImageUrl(String imageUrl) { // Setter untuk imageUrl
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Item{" +
               "id=" + (id != null ? id.toHexString() : "null") +
               ", name='" + name + '\'' +
               ", quantity=" + quantity +
               ", imageUrl='" + imageUrl + '\'' + // Tambahkan ke toString
               '}';
    }
}