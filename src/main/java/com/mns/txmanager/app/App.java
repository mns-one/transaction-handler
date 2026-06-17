package com.mns.txmanager.app;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.mns.txmanager.lockManager.LockManager;
import com.mns.txmanager.recovery.RecoveryManager;
import com.mns.txmanager.storage.InMemoryStorageEngine;
import com.mns.txmanager.storage.StorageEngine;
import com.mns.txmanager.transaction.Transaction;
import com.mns.txmanager.wal.WalManager;


@Component
public class App {

    private final StorageEngine storage;
    private final LockManager lockManager;

    App(StorageEngine storage, LockManager lockManager) {
        this.storage = storage;
        this.lockManager = lockManager;
    }

    public void start() {

        // Recovery test
        StorageEngine recoveredDb = new InMemoryStorageEngine();
        WalManager walManager = new WalManager("wal.log");

        RecoveryManager rm = new RecoveryManager(Path.of("wal.log"), recoveredDb, walManager);

        try {
            rm.recover();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println( recoveredDb.get("user") ); // should print john
        System.out.println(storage.get("balance")); // should print null


        // // check wal generation
        // WalManager walManager = new WalManager("wal.log");
        // Transaction tx = new Transaction("tx1", storage, lockManager, walManager);

        // tx.set("user", "john");
        // tx.set("balance", "1000");

        // try {
        //     tx.commit();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }


        // // test for deadlock detection
        // Transaction tx1 = new Transaction(storage, lockManager, "tx1");
        // Transaction tx2 = new Transaction(storage, lockManager, "tx2");

        // tx1.set("A", "100");
        // tx2.set("B", "200");

        // tx1.set("B", "300");
        // tx2.set("A", "400");

    }

}
