package com.sda5.double2app.models;

import java.util.Objects;
import java.util.UUID;

public class Account {
    private String id;
    private String userID;
    private boolean isInternalAccount;
    private String ownerName;
    private String email;
    private String phoneNumber;
    private Double monthlyBudget;
    private Double monthlySave;
    private String tokenID;

    public Account(){}

    public Account(boolean isInternalAccount, String ownerName, String email,
                   String phoneNumber, Double monthlyBudget, Double monthlySave) {
        this.isInternalAccount = isInternalAccount;
        this.ownerName = ownerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.monthlyBudget = monthlyBudget;
        this.monthlySave = monthlySave;
        this.id = UUID.randomUUID().toString();
    }

    public Account(boolean isInternalAccount,String ownerName, String email) {
        this.isInternalAccount = isInternalAccount;
        this.ownerName = ownerName;
        this.email = email;
        this.id = UUID.randomUUID().toString();
    }

    public Account( boolean isInternalAccount, String ownerName, String email, String tokenID) {
        this.isInternalAccount = isInternalAccount;
        this.ownerName = ownerName;
        this.email = email;
        this.tokenID = tokenID;
        this.id = UUID.randomUUID().toString();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setDocumentID(String documentID) {
        this.id = documentID;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(Double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public Double getMonthlySave() {
        return monthlySave;
    }

    public void setMonthlySave(Double monthlySave) {
        this.monthlySave = monthlySave;
    }

    public boolean isInternalAccount() {
        return isInternalAccount;
    }

    public void setInternalAccount(boolean internalAccount) {
        isInternalAccount = internalAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return  ownerName;
    }
}
