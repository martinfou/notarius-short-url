package com.notarius.shorturl.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

public class UrlUtil {

    public static String generateShortUrl(String fullUrl) {
        if (fullUrl == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(fullUrl.getBytes(StandardCharsets.UTF_8));
            String hash = Hex.encodeHexString(encodedhash);
            return hash.length() > 10 ? hash.substring(0, 10) : hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to find SHA-256 algorithm", e);
        }
    }
}
