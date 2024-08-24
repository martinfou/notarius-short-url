package com.notarius.shorturl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UrlTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Url getUrlSample1() {
        return new Url().id(1L).shortUrl("shortUrl1").fullUrl("fullUrl1");
    }

    public static Url getUrlSample2() {
        return new Url().id(2L).shortUrl("shortUrl2").fullUrl("fullUrl2");
    }

    public static Url getUrlRandomSampleGenerator() {
        return new Url().id(longCount.incrementAndGet()).shortUrl(UUID.randomUUID().toString()).fullUrl(UUID.randomUUID().toString());
    }
}
