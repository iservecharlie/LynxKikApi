package org.kik.bot.example;

import org.apache.log4j.Logger;
import org.kik.bot.core.KikClient;
import org.kik.bot.core.handler.DataHandler;
import org.kik.bot.core.handler.DetailedRosterMessageHandler;
import org.kik.bot.core.handler.MessageHandler;
import org.kik.bot.core.handler.RosterHandler;
import org.kik.bot.core.translator.JidTranslator;
import org.kik.bot.example.behaviour.Echo;
import org.kik.bot.example.behaviour.Listen;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class EchoBot {
    private static final Logger LOGGER = Logger.getLogger(EchoBot.class);

    private final String username;
    private final String password;

    public EchoBot(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void start() throws IOException, XmlPullParserException {
        KikClient kikClient = new KikClient();
        kikClient.login_to_kik_server(username, password);
        //set handlers
        RosterHandler rosterHandler = new RosterHandler(kikClient);
        JidTranslator jidTranslator = new JidTranslator(rosterHandler);
        MessageHandler messageHandler = new DetailedRosterMessageHandler(jidTranslator);

        messageHandler.addBotBehaviour(new Listen());
        messageHandler.addBotBehaviour(new Echo(kikClient));
        DataHandler dataHandler = new DataHandler(rosterHandler, messageHandler);
        kikClient.setOnDataReceived(dataHandler::handleData);

        kikClient.start();
    }
}
