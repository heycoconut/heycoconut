package org.noixdecoco.app.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.dto.AuthInfoDTO;
import org.noixdecoco.app.dto.ChannelDTO;
import org.noixdecoco.app.dto.MessageDTO;
import org.noixdecoco.app.dto.SlackAction;
import org.noixdecoco.app.service.SlackService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
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
        sendMessage(channel, text, false);
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
    public ChannelDTO getChannelInfo(String channelId) {
        HttpEntity entity = new HttpEntity(createHttpHeaders());
        Map<String, String> param = new HashMap<>();
        param.put("channel", channelId);
        ResponseEntity<ChannelDTO> response = restTemplate.exchange(
                SLACK_API_URL + SlackAction.CHANNEL_INFO, HttpMethod.GET, entity, ChannelDTO.class, param);
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

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }
}
