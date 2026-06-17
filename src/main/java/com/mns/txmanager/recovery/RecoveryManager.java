package com.mns.txmanager.recovery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mns.txmanager.storage.StorageEngine;
import com.mns.txmanager.wal.LogEntry;
import com.mns.txmanager.wal.WalManager;

public class RecoveryManager {

    private final Path walFile;
    private final StorageEngine storage;
    private final WalManager walManager;

    public RecoveryManager(Path walFile, StorageEngine storage, WalManager walManager) {
        this.walFile = walFile;
        this.storage = storage;
        this.walManager = walManager;
    }

    public void recover() throws IOException {

        List<String> lines = Files.readAllLines(walFile);
        Set<String> committedTx = new HashSet<>();
        List<LogEntry> entries = new ArrayList<>();

        // read log records from walFile
        // build set of commited transactions
        for(String line : lines){

            LogEntry entry = walManager.parse(line);

            if(entry.getOperation().equals("COMMIT")) {
                    committedTx.add(entry.getTxId());
            }
            else {
                entries.add(entry);
            }

        }

        // re apply only commited transaction changes
        for(LogEntry entry : entries){

            if(!committedTx.contains(entry.getTxId())) continue;

            if(entry.getOperation().equals("SET")) {
                storage.set(
                        entry.getKey(),
                        entry.getValue()
                );
            }

            if(entry.getOperation().equals("DELETE")) {
                storage.delete(
                        entry.getKey()
                );
            }

        }


    }


}
