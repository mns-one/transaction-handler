package com.mns.txmanager.transaction;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mns.txmanager.lockManager.LockManager;
import com.mns.txmanager.storage.StorageEngine;
import com.mns.txmanager.wal.LogEntry;
import com.mns.txmanager.wal.WalManager;


public class Transaction {

    private final Map<String, String> pendingChanges = new HashMap<>();
    private final Set<String> deletedKeys = new HashSet<>();
    private final StorageEngine storage;
    private final LockManager lockManager;
    private final WalManager walManager;
    private final String txId;

    public Transaction(String txId, StorageEngine storage, LockManager lockManager, WalManager walManager){
        this.storage = storage;
        this.lockManager = lockManager;
        this.txId = txId;
        this.walManager = walManager;
    }

    // acquire lock before updating values
    public void set(String key, String value) {
        try{
            lockManager.acquireLock(key, txId);
        }
        catch (Exception e){
            System.out.println(txId + " -> " + e.getMessage());
            return;
        }

        deletedKeys.remove(key);
        pendingChanges.put(key, value);
    }

    // for fetching values, check transaction space first else main storage
    public String get(String key) {
        if(deletedKeys.contains(key)){
            return null;
        }
        if(pendingChanges.containsKey(key)){
            return pendingChanges.get(key);
        }
        return storage.get(key);
    }

    // track deleted keys seperately
    public void delete(String key) {
        try{
            lockManager.acquireLock(key, txId);
        }
        catch (Exception e){
            System.out.println(txId + " -> " + e.getMessage());
        }

        pendingChanges.remove(key);
        deletedKeys.add(key);
    }

    // clear any tracked changes on rollback n release all locks
    public void rollback() {
        pendingChanges.clear();
        deletedKeys.clear();
        lockManager.releaseLocks(txId);
    }

    // apply tracked changes from both sets to main storage
    // clear sets and release all locks
    public void commit() throws IOException {

        // first write all changes from deletedKeys n pendingChanges to walFile
        for(String key : deletedKeys) {
            walManager.append(
                new LogEntry(
                    txId,
                    "DELETE",
                    key,
                    ""
                )
            );
        }

        for(Map.Entry<String,String> entry : pendingChanges.entrySet()) {
            walManager.append(
                new LogEntry(
                    txId,
                    "SET",
                    entry.getKey(),
                    entry.getValue()
                )
            );
        }

        walManager.append(
            new LogEntry(
                txId,
                "COMMIT",
                "",
                ""
            )
        );

        // after walFile is done, finally apply changes to storage
        for(String entry : deletedKeys){
            storage.delete(entry);
        }

        for(Map.Entry<String, String> entry : pendingChanges.entrySet()){
            storage.set(entry.getKey(), entry.getValue());
        }
        
        pendingChanges.clear();
        deletedKeys.clear();
        lockManager.releaseLocks(txId);

    }

}

