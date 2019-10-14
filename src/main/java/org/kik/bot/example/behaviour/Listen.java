package org.kik.bot.example.behaviour;

import org.apache.log4j.Logger;
import org.kik.bot.core.behaviour.BotBehaviour;
import org.kik.bot.core.model.GroupChatMessage;
import org.kik.bot.core.model.PersonalChatMessage;

public class Listen implements BotBehaviour {
    private static final Logger LOGGER = Logger.getLogger(Listen.class);

    @Override
    public void onPersonalChatMessage(PersonalChatMessage message) {
        String displayMessage = String.format("[PM] '%s' said '%s'.", message.getDisplayName(), message.getMessage());
        LOGGER.info(displayMessage);
    }

    @Override
    public void onGroupChatMessage(GroupChatMessage message) {
        String displayMessage = String.format("[GM] from '%s' by '%s' said '%s'.", message.getGroupName(), message.getMemberName(), message.getMessage());
        LOGGER.info(displayMessage);
    }
}