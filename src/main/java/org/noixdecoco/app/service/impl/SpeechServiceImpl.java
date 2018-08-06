package org.noixdecoco.app.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.dto.MeMessageDTO;
import org.noixdecoco.app.service.SpeechService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpeechServiceImpl implements SpeechService {
	
	private static final Logger LOGGER = LogManager.getLogger(SpeechService.class);
	
	@Value("${bot.auth.token}")
	private String authToken;

	@Override
	public void sendMessage(String channel, String text) {
		MeMessageDTO message = new MeMessageDTO();
		message.setChannel(channel);
		message.setText(text);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json"); 
		headers.set("Authorization", "Bearer " + authToken); 
		HttpEntity<MeMessageDTO> request = new HttpEntity<>(message, headers);
		String response = new RestTemplate().postForObject("https://slack.com/api/chat.meMessage", request, String.class);
		LOGGER.info("Response: " + response);

	}

}
