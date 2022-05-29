package com.sda5.double2app.models;

import java.io.Serializable;
import java.util.UUID;

public class Category implements Serializable {
    private String id;
    private String title;
    private int maxBudget;

    public Category() { }

    public Category(String title, int maxBudget) {
        this.title = title;
        this.maxBudget = maxBudget;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(int maxBudget) {
        this.maxBudget = maxBudget;
    }

    @Override
    public String toString() {
        return title;
    }
}
