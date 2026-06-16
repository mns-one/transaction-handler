package com.mns.txmanager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mns.txmanager.app.App;


@SpringBootApplication
public class TxManagerApplication implements CommandLineRunner {

	private final App app;

	TxManagerApplication(App app) {
		this.app = app;
	}

	public static void main(String[] args) {
		SpringApplication.run(TxManagerApplication.class, args);
	}

	@Override
    public void run(String... args) {
        app.start();
    }

}
