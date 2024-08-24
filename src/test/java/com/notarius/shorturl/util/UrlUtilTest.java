package com.notarius.shorturl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UrlUtilTest {

    @Test
    public void generateShortUrl_returnsShortUrlForGivenFullUrl() {
        String fullUrl = "https://www.example.com";
        String shortUrl = UrlUtil.generateShortUrl(fullUrl);
        assertTrue(shortUrl.length() <= 10);
    }

    @Test
    public void generateShortUrl_returnsDifferentShortUrlsForDifferentFullUrls() {
        String fullUrl1 = "https://www.example1.com";
        String fullUrl2 = "https://www.example2.com";
        String shortUrl1 = UrlUtil.generateShortUrl(fullUrl1);
        String shortUrl2 = UrlUtil.generateShortUrl(fullUrl2);
        assertTrue(!shortUrl1.equals(shortUrl2));
    }

    @Test
    public void generateShortUrl_returnsSameShortUrlForSameFullUrl() {
        String fullUrl = "https://www.example.com";
        String shortUrl1 = UrlUtil.generateShortUrl(fullUrl);
        String shortUrl2 = UrlUtil.generateShortUrl(fullUrl);
        assertEquals(shortUrl1, shortUrl2);
    }

    @Test
    public void generateShortUrl_returnsEmptyStringforNullFullUrl() {
        String fullUrl = null;
        String shortUrl1 = UrlUtil.generateShortUrl(fullUrl);
        assertEquals(shortUrl1, "");
    }
}
