package com.mns.txmanager.storage;

public interface StorageEngine {

    void set(String key, String value);

    String get(String key);

    void delete(String key);

}

