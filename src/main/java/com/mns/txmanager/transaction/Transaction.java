package com.mns.txmanager.transaction;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.mns.txmanager.storage.StorageEngine;


@Component
public class Transaction {

    private final Map<String, String> pendingChanges = new HashMap<>();
    private final StorageEngine storage;

    Transaction( StorageEngine storage ){
        this.storage = storage;
    }

    public void set(String key, String value) {
        pendingChanges.put(key, value);
    }

    public String get(String key) {
        if(pendingChanges.containsKey(key)){
            return pendingChanges.get(key);
        }
        return storage.get(key);
    }

    public void rollback() {
        pendingChanges.clear();
    }

    public void commit() {

        for(Map.Entry<String, String> entry : pendingChanges.entrySet()){
            storage.set(entry.getKey(), entry.getValue());
        }
        pendingChanges.clear();

    }

}

