package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("アプリケーションを起動します");
        SpringApplication.run(Main.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("アプリケーションが正常に起動しました");
    }

    @EventListener(ContextClosedEvent.class)
    public void onApplicationShutdwn() {
        log.info("アプリケーションを終了します");
    }
}