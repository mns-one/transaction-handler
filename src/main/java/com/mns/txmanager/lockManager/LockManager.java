package com.mns.txmanager.lockManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;


@Component
public class LockManager {

    // store acquired locks by transaction
    private final Map<String, String> locks = new ConcurrentHashMap<>();

    // maintain dependency list for waiting transactions
    private final Map<String, Set<String>> waitForGraph = new ConcurrentHashMap<>();

    // use graph dfs to check for cycle among waiting transactions and detect deadlock siutation 
    public boolean hasCycle(String node, Set<String> vis, Set<String> path) {

        if(path.contains(node)) return true;
        if(vis.contains(node)) return false;

        vis.add(node);
        path.add(node);

        Set<String> neiList =  waitForGraph.getOrDefault(node,Collections.emptySet());

        for(String nei : neiList){
            if(hasCycle(nei, vis, path)) return true;  
        }
        
        path.remove(node);
        return false;

    }

    // add edge for every waiting transaction
    public void addEdge(String node1, String node2) {
        waitForGraph
            .computeIfAbsent(
                node1,
                k -> new HashSet<>()
            )
            .add(node2);

            Set<String> vis = new HashSet<>();
            Set<String> path = new HashSet<>();

            if(hasCycle(node1, vis, path)){
                throw new IllegalStateException( "Deadlock detected");
            }
    }

    // acquire lock by checking for lock availability and dependency cycle
    public void acquireLock(String key, String txId) {

        String owner = locks.get(key);

        if(owner != null && !owner.equals(txId)) {
            addEdge(txId, owner);
            throw new IllegalStateException( "Key '" + key + "' is locked by another transaction");
        }

        locks.putIfAbsent(key, txId);
        System.out.println(txId + " -> " + locks.get(key));

    }

    // on release, remove all dependency edges and acquired locks by transaction
    public void releaseLocks(String txId) {

        locks.entrySet().removeIf(
            entry ->
                txId.equals(
                    entry.getValue()
                )
        );

        waitForGraph.remove(txId);

        waitForGraph.values()
            .forEach(
                neighbors ->
                    neighbors.remove(txId)
        );

    }
    
}
