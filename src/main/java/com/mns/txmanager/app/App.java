package com.mns.txmanager.app;

import org.springframework.stereotype.Component;

import com.mns.txmanager.storage.StorageEngine;
import com.mns.txmanager.transaction.Transaction;

@Component
public class App {

    private final Transaction tx;
    private final StorageEngine storage;

    App(Transaction tx, StorageEngine storage) {
        this.tx = tx;
        this.storage = storage;
    }

    public void start() {

        storage.set("user", "alice");

        tx.set("user", "john");

        System.out.println( tx.get("user") );

    }

}
