package org.noixdecoco.app.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class SlackSignatureUtil {

    private static final Logger LOGGER = LogManager.getLogger(SlackSignatureUtil.class);

    @Value("${bot.signing.secret}")
    private String signingSecret;

    private static final String HMAC_SHA256 = "HmacSHA256";

    public boolean signatureIsValid(HttpHeaders headers, String data) {
        String requestTimestamp = headers.get("X-Slack-Request-Timestamp").get(0);
        String requestSignature = headers.get("X-Slack-Signature").get(0);
        LOGGER.debug("requestSignature = " + requestSignature);
        LOGGER.debug("requestTimestamp = " + requestTimestamp);
        String fullEncryptData = "v0:" + requestTimestamp + ":" + data;
        try {
            String encode = "v0=" + encode(fullEncryptData);
            return encode.equals(requestSignature);
        } catch (Exception e) {
            return false;
        }
    }

    private String encode(String data)  {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(signingSecret.getBytes("UTF-8"), HMAC_SHA256);
            mac.init(secretKey);
            return Hex.encodeHexString(mac.doFinal(data.getBytes("UTF-8")));
        } catch(UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException e) {
            LOGGER.error(e);
        }
        return null;
    }
}
