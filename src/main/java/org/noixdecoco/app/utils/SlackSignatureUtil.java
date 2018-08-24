package org.noixdecoco.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.noixdecoco.app.rest.MainREST;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class SlackSignatureUtil {

    private static final Logger LOGGER = LogManager.getLogger(SlackSignatureUtil.class);

    @Value("${bot.signing.secret}")
    private String signingSecret;

    public boolean signatureIsValid(HttpHeaders headers, String data) {
        String requestTimestamp = headers.get("X-Slack-Request-Timestamp").get(0);
        String requestSignature = headers.get("X-Slack-Signature").get(0);
        LOGGER.info("requestSignature = " + requestSignature);
        LOGGER.info("requestTimestamp = " + requestTimestamp);
        LOGGER.info("data = [" + data + "]");
        LOGGER.info("signingSecret = " + signingSecret);
        return true;
    }

    private String encode(String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(signingSecret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }
}
