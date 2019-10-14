package org.kik.bot.core.handler;

import net.lynx.client.objects.Node;
import org.apache.log4j.Logger;
import org.kik.bot.core.KikClient;
import org.kik.bot.core.model.Chat;
import org.kik.bot.core.model.GroupChat;
import org.kik.bot.core.model.PersonChat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.kik.bot.core.constants.IqType.*;

public class RosterHandler {
    private static final Logger LOG = Logger.getLogger(RosterHandler.class);

    private final KikClient kikClient;

    private Map<String, Chat> chatInteractionMap;
    private Map<String, String> groupChatMemberGroupChatMap;

    public RosterHandler(KikClient kikClient) {
        LOG.debug("[System] Initializing Empty Roster.");
        this.kikClient = kikClient;
        chatInteractionMap = new HashMap<>();
        groupChatMemberGroupChatMap = new HashMap<>();
    }

    public Map<String, Chat> getChatInteractionMap() {
        return chatInteractionMap;
    }

    public Map<String, String> getGroupChatMemberGroupChatMap() {
        return groupChatMemberGroupChatMap;
    }

    public void handle(Node node) {
        Node query = node.getFirstChildByName(QUERY);
        String queryType = query.getAttribute(XMLNS);
        if (XMLNS_ROSTER_REQUEST.equalsIgnoreCase(queryType)) {
            updateRoster(query);
        } else if(XMLNS_MEMBER_DETAIL_REQUEST.equalsIgnoreCase(queryType)) {
            updateChatGroupMember(query);
        } else {
            LOG.warn("[System] Unexpected <iq> query: " + node);
        }
    }

    private void updateRoster(Node rosterListWrapper) {
        for(Node rosterList: rosterListWrapper.getChildren()) {
            String nodeName = rosterList.getName();
            if(ROSTER_REQUEST_PERSON_CHAT_NODE_NAME.equalsIgnoreCase(nodeName)) {
                PersonChat item = new PersonChat(rosterList);
                chatInteractionMap.put(item.getJid(), item);
                LOG.debug("[System] Registered/Updated PersonChat[" + item.getJid() + "]");
            } else if(ROSTER_REQUEST_GROUP_CHAT_NODE_NAME.equalsIgnoreCase(nodeName)) {
                GroupChat group = new GroupChat(rosterList);
                String groupJid = group.getJid();
                Set<String> groupMemberJids = group.getMembers().keySet();
                for(String memberJid: groupMemberJids) {
                    addToMemberGroupMap(memberJid, groupJid);

                }
                chatInteractionMap.put(groupJid, group);
                LOG.debug("[System] Registered/Updated GroupChat[" + groupJid + "]");
            } else if("remove-group".equalsIgnoreCase(nodeName)) {
                //todo: implement remove chat interaction: remove entry from chatInteractionMap based on jid
            } else {
                LOG.warn("[System] Unknown Roster request data: " + rosterListWrapper.toString());
            }
        }
    }

    public void requestMemberDetails(String memberJid) {
        kikClient.sendMemberDetailRequest(memberJid);
    }

    public void addToMemberGroupMap(String memberJid, String groupJid) {
        if(groupChatMemberGroupChatMap == null) {
            groupChatMemberGroupChatMap = new HashMap<>();
        }
        groupChatMemberGroupChatMap.put(memberJid, groupJid);
        requestMemberDetails(memberJid);
    }

    public void removeMemberInGroupMap(String memberJid, String groupJid) {
        groupChatMemberGroupChatMap.remove(memberJid);
        GroupChat groupChat = (GroupChat) chatInteractionMap.get(groupJid);
        groupChat.removeMember(memberJid);
    }

    private void updateChatGroupMember(Node query) {
        Node success = query.getFirstChildByName(MEMBER_DETAIL_REQUEST_RESULT_WRAPPER);
        if(success != null) {
            Node memberDetial = success.getFirstChildByName(MEMBER_DETAIL_REQUEST_NODE_NAME);
            String memberJid = memberDetial.getAttribute("jid");
            String groupJid = groupChatMemberGroupChatMap.get(memberJid);
            if(groupJid == null || "".equalsIgnoreCase(groupJid)) {
                LOG.warn("[System] Couldn't find groupJid for ChatGroupMember[" + memberJid + "].");
                return;
            }
            GroupChat group = (GroupChat) chatInteractionMap.get(groupJid);
            group.updateMemberDetails(memberJid, memberDetial);
            chatInteractionMap.put(groupJid, group);
            LOG.debug("[System] Updated Member[" + memberJid + "] for ChatGroup[" + groupJid + "].");
        }
    }

    public void requestFullChatRosterDetails() {
        kikClient.sendFullChatRosterRequest();
    }
}
