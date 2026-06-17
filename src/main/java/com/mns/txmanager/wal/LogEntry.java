package com.mns.txmanager.wal;

import lombok.Data;

@Data
public class LogEntry {

    private final String txId;
    private final String operation;
    private final String key;
    private final String value;

    public LogEntry(String txId, String operation, String key, String value) {
        this.txId = txId;
        this.operation = operation;
        this.key = key;
        this.value = value;
    }
    
}
