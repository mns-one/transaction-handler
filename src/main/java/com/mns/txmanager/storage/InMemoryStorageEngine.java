package com.mns.txmanager.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;


@Component
public class InMemoryStorageEngine implements StorageEngine {

    private final Map<String, String> data = new ConcurrentHashMap<>();

    public void set(String key, String value){
        data.put(key, value);
    }
    
    public String get(String key){
        return data.get(key);
    }

    public void delete(String key){
        data.remove(key);
    }

}
