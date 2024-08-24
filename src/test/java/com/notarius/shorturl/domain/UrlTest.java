package com.notarius.shorturl.domain;

import static com.notarius.shorturl.domain.UrlTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.notarius.shorturl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UrlTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Url.class);
        Url url1 = getUrlSample1();
        Url url2 = new Url();
        assertThat(url1).isNotEqualTo(url2);

        url2.setId(url1.getId());
        assertThat(url1).isEqualTo(url2);

        url2 = getUrlSample2();
        assertThat(url1).isNotEqualTo(url2);
    }
}
