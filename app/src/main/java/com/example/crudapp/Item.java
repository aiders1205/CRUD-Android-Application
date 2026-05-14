package com.example.crudapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item {
    private int id;
    private String name;
    private String description;
    private String timestamp;
    private String category;

    public Item(int id, String name, String description, String timestamp, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.timestamp = timestamp;
        this.category = category;
    }

    public Item(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // Default constructor for DatabaseHelper
    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
