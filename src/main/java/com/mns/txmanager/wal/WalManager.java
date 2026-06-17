package com.mns.txmanager.wal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


public class WalManager {

    private final Path walFile;

    public WalManager(String fileName) {
        this.walFile = Path.of(fileName);
    }
    
    // write logEntry to walFile
    public void append(LogEntry entry) throws IOException {

        Files.writeString(
            walFile,
            format(entry),
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );

    }

    private String format(LogEntry entry) {

        return String.format(
            "%s|%s|%s|%s%n",
            entry.getTxId(),
            entry.getOperation(),
            entry.getKey(),
            entry.getValue()
        );

    }

    public LogEntry parse(String line) {

        String[] parts =
                line.split("\\|", -1);

        return new LogEntry(
                parts[0],
                parts[1],
                parts[2],
                parts[3]
        );
    }

    
}
