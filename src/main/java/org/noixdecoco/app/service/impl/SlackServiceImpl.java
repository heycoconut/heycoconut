package org.noixdecoco.app.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.dto.*;
import org.noixdecoco.app.service.SlackService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SlackServiceImpl implements SlackService {

    private static final Logger LOGGER = LogManager.getLogger(SlackService.class);

    @Value("${bot.auth.token}")
    private String authToken;

    private RestTemplate restTemplate;

    private static final String SLACK_API_URL = "https://slack.com/api/";

    private String botUserId = null;

    public SlackServiceImpl() {
        restTemplate = new RestTemplate();
    }

    @Override
    public void sendMessage(String channel, String text) {
        sendMessage(channel, text, false, null);
    }

    @Override
    public void sendMessage(String channel, String text, boolean ephemeral, String toUser) {
        MessageDTO message = new MessageDTO();
        message.setChannel(channel);
        message.setText(text);
        if (ephemeral) {
            message.setUser(toUser);
        }
        String response = restTemplate.postForObject(SLACK_API_URL + (ephemeral ? SlackAction.POST_EPHEMERAL : SlackAction.POST_MESSAGE), new HttpEntity(message, createHttpHeaders()), String.class);
        LOGGER.info("Response: " + response);
    }

    @Override
    public void addReaction(String channel, String timestamp, String emoji) {
        ReactionDTO reaction = new ReactionDTO();
        reaction.setChannel(channel);
        reaction.setTimestamp(timestamp);
        reaction.setName(emoji);
        String response = restTemplate.postForObject(SLACK_API_URL + SlackAction.ADD_REACTION, new HttpEntity(reaction, createHttpHeaders()), String.class);
        LOGGER.info("Response: " + response);
    }

    @Override
    public ChannelDTO getChannelInfo(String channelId) {
        HttpEntity entity = new HttpEntity(createHttpHeaders());
        Map<String, String> param = new HashMap<>();
        param.put("channel", channelId);
        ResponseEntity<ChannelDTO> response = restTemplate.exchange(
                SLACK_API_URL + SlackAction.CHANNEL_INFO, HttpMethod.GET, entity, ChannelDTO.class, param);
        return response.getBody();
    }

    @Override
    public GroupDTO getGroupInfo(String groupId) {
        LOGGER.info("Getting group: " + groupId);
        HttpEntity entity = new HttpEntity(createHttpHeaders());
        Map<String, String> param = new HashMap<>();
        param.put("channel", groupId);
        ResponseEntity<GroupDTO> response = restTemplate.exchange(
                SLACK_API_URL + SlackAction.GROUP_INFO, HttpMethod.GET, entity, GroupDTO.class, param);
        LOGGER.info("Response:" + response.getBody() + ", id:" + response.getBody().getId() + " " + response.getBody().getMembers().size() + " members, name:" + response.getBody().getName() );
        return response.getBody();
    }

    @Override
    public String getBotUserId() {
        if (botUserId == null) {
            ResponseEntity<AuthInfoDTO> authInfoResponse = restTemplate.exchange(SLACK_API_URL + SlackAction.AUTH_INFO, HttpMethod.GET, new HttpEntity(createHttpHeaders()), AuthInfoDTO.class);
            if (HttpStatus.OK.equals(authInfoResponse.getStatusCode())) {
                botUserId = authInfoResponse.getBody().getUserId();
            } else {
                LOGGER.error("Failed to retrieve bot's userId");
            }
        }
        return botUserId;
    }

    @Override
    public ChannelListDTO getChannelsBotIsIn() {
        HttpEntity entity = new HttpEntity(createHttpHeaders());
        Map<String, String> param = new HashMap<>();
        param.put("types", "private_channel,public_channel");
        ResponseEntity<ChannelListDTO> response = restTemplate.exchange(
                SLACK_API_URL + SlackAction.LIST_CHANNELS, HttpMethod.GET, entity, ChannelListDTO.class, param);
        return response.getBody();
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }
}
