package com.mns.txmanager.app;

import org.springframework.stereotype.Component;

import com.mns.txmanager.lockManager.LockManager;
import com.mns.txmanager.storage.StorageEngine;
import com.mns.txmanager.transaction.Transaction;


@Component
public class App {

    private final StorageEngine storage;
    private final LockManager lockManager;

    App(StorageEngine storage, LockManager lockManager) {
        this.storage = storage;
        this.lockManager = lockManager;
    }

    public void start() {

        Transaction tx1 = new Transaction(storage, lockManager, "tx1");
        Transaction tx2 = new Transaction(storage, lockManager, "tx2");

        // test for deadlock detection
        tx1.set("A", "100");
        tx2.set("B", "200");

        tx1.set("B", "300");
        tx2.set("A", "400");

    }

}
