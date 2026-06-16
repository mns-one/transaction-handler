package com.mns.txmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mns.txmanager.storage.InMemoryStorageEngine;
import com.mns.txmanager.storage.StorageEngine;
import com.mns.txmanager.transaction.Transaction;


public class TxManagerApplication {

	public static void main(String[] args) {

		Transaction tx = new Transaction();

		tx.set("user", "john");

		System.out.println(tx.get("user"));

		tx.rollback();

		System.out.println(tx.get("user"));

	}

}
