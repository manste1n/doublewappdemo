package com.sda5.double2app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Expense implements Serializable {
    private String id;
    private String title;
    private Double amount;
    private String category;
    private String payerAccountId;
    private String groupId;
    private String date;
    private Long dateMillisec;
    private ArrayList<String> expenseAccountIds;
    private boolean isRecursive;
    private Long logDate;

    public Expense() {
    }

    public Expense(String title, Double amount, String category, String payerAccountId, String groupId, String date,
                   Long dateMillisec, ArrayList<String> expenseAccountIds, boolean isRecursive) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.payerAccountId = payerAccountId;
        this.groupId = groupId;
        this.date = date;
        this.dateMillisec = dateMillisec;
        this.expenseAccountIds = expenseAccountIds;
        this.isRecursive = isRecursive;
        this.id = UUID.randomUUID().toString();
        this.logDate = System.currentTimeMillis();
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPayerAccountId() {
        return payerAccountId;
    }

    public void setPayerAccountId(String payerAccountId) {
        this.payerAccountId = payerAccountId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getExpenseAccountIds() {
        return expenseAccountIds;
    }

    public void setExpenseAccountIds(ArrayList<String> expenseAccountIds) {
        this.expenseAccountIds = expenseAccountIds;
    }

    public boolean isRecursive() {
        return isRecursive;
    }

    public void setRecursive(boolean recursive) {
        isRecursive = recursive;
    }

    public Long getDateMillisec() {
        return dateMillisec;
    }

    public void setDateMillisec(Long dateMillisec) {
        this.dateMillisec = dateMillisec;
    }

    public Long getLogDate() {
        return logDate;
    }

    public void setLogDate(Long logDate) {
        this.logDate = logDate;
    }
}
