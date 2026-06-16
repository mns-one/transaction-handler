package com.mns.txmanager.transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.mns.txmanager.storage.StorageEngine;


@Component
public class Transaction {

    private final Map<String, String> pendingChanges = new HashMap<>();
    private final Set<String> deletedKeys = new HashSet<>();
    private final StorageEngine storage;

    Transaction( StorageEngine storage ){
        this.storage = storage;
    }

    public void set(String key, String value) {
        deletedKeys.remove(key);
        pendingChanges.put(key, value);
    }

    public String get(String key) {
        if(deletedKeys.contains(key)){
            return null;
        }
        if(pendingChanges.containsKey(key)){
            return pendingChanges.get(key);
        }
        return storage.get(key);
    }

    public void delete(String key) {
        pendingChanges.remove(key);
        deletedKeys.add(key);
    }

    public void rollback() {
        pendingChanges.clear();
        deletedKeys.clear();
    }

    public void commit() {

        for(String entry : deletedKeys){
            storage.delete(entry);
        }

        for(Map.Entry<String, String> entry : pendingChanges.entrySet()){
            storage.set(entry.getKey(), entry.getValue());
        }
        pendingChanges.clear();
        deletedKeys.clear();

    }

}

