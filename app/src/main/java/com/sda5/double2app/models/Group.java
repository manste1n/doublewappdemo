package com.sda5.double2app.models;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Group {

    private FirebaseAuth mAuth;
    private String adminUserId;
    private String id;
    private String name;
    private List<String> accountIdList;
    private String adminAccountId;
    private HashMap<String, Double> balance = new HashMap<>();

    public Group(){}

    public Group(String name, List<String> list , String adminAccountId) {
        mAuth = FirebaseAuth.getInstance();
        this.name = name;
        this.accountIdList = list;
        for(String accountId: list){
            this.balance.put(accountId, 0.0);
        }
        this.adminUserId = mAuth.getCurrentUser().getUid();
        this.id = UUID.randomUUID().toString();
        this.adminAccountId = adminAccountId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAccountIdList() {
        Set<String> set = new HashSet<>(accountIdList);
        accountIdList.clear();
        accountIdList.addAll(set);
        return accountIdList;
    }

    public void setAccountIdList(List<String> accountIdList) {
        this.accountIdList = accountIdList;
    }

    public String getAdminUserId() {
        return adminUserId;
    }

    public String getAdminAccountId() {
        return adminAccountId;
    }

    public void setAdminAccountId(String adminAccountId) {
        this.adminAccountId = adminAccountId;
    }

    public HashMap<String, Double> getBalance() {
        return balance;
    }

    public void setBalance(HashMap<String, Double> balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return name;
    }
}
