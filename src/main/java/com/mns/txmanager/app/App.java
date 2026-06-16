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

        storage.set("user", "john");

        tx.delete("user");

        System.out.println( storage.get("user") ); // shows john

        tx.commit();

        System.out.println( storage.get("user") ); // shows null

    }

}
