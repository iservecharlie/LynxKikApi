package org.kik.bot;

import org.apache.log4j.Logger;
import org.kik.bot.example.EchoBot;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Client implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(Client.class);
    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            EchoBot echoBot = new EchoBot(args[0], args[1]);
            echoBot.start();
        } catch (Exception e) {
            logger.error("Failed to start KikClient", e);
        }
    }
}