package com.mns.txmanager.transaction;

import java.util.HashMap;
import java.util.Map;

public class Transaction {

    private final Map<String, String> pendingChanges = new HashMap<>();

    public void set(String key, String value) {
        pendingChanges.put(key, value);
    }

    public String get(String key) {
        return pendingChanges.getOrDefault(key, null);
    }

    public void rollback() {
        pendingChanges.clear();
    }

}

