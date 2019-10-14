package org.kik.bot.example.behaviour;

import org.apache.log4j.Logger;
import org.kik.bot.core.KikClient;
import org.kik.bot.core.behaviour.BotBehaviour;
import org.kik.bot.core.model.GroupChatMessage;
import org.kik.bot.core.model.PersonalChatMessage;

import java.io.IOException;

public class Echo implements BotBehaviour {
    private static final Logger LOGGER = Logger.getLogger(Echo.class);
    private final KikClient kikClient;

    public Echo(KikClient kikClient) {
        this.kikClient = kikClient;
    }

    @Override
    public void onPersonalChatMessage(PersonalChatMessage message) {
        try {
            kikClient.sendMessage(String.format("You said '%s'.", message.getMessage()), message.getJid(), false);
        } catch (IOException e) {
            LOGGER.error("Cannot deliver message to " + message.getJid(), e);
        }
    }

    @Override
    public void onGroupChatMessage(GroupChatMessage message) {
        //do nothing
    }
}