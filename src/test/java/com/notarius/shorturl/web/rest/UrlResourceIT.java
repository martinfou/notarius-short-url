package com.notarius.shorturl.web.rest;

import static com.notarius.shorturl.domain.UrlAsserts.*;
import static com.notarius.shorturl.web.rest.TestUtil.createUpdateProxyForBean;
import static com.notarius.shorturl.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notarius.shorturl.IntegrationTest;
import com.notarius.shorturl.domain.Url;
import com.notarius.shorturl.repository.UrlRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UrlResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UrlResourceIT {

    private static final String DEFAULT_SHORT_URL = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_URL = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_URL = "AAAAAAAAAA";
    private static final String UPDATED_FULL_URL = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATION_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATION_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_EXPIRATION_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_EXPIRATION_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/urls";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUrlMockMvc;

    private Url url;

    private Url insertedUrl;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Url createEntity(EntityManager em) {
        Url url = new Url()
            .shortUrl(DEFAULT_SHORT_URL)
            .fullUrl(DEFAULT_FULL_URL)
            .creationDateTime(DEFAULT_CREATION_DATE_TIME)
            .expirationDateTime(DEFAULT_EXPIRATION_DATE_TIME);
        return url;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Url createUpdatedEntity(EntityManager em) {
        Url url = new Url()
            .shortUrl(UPDATED_SHORT_URL)
            .fullUrl(UPDATED_FULL_URL)
            .creationDateTime(UPDATED_CREATION_DATE_TIME)
            .expirationDateTime(UPDATED_EXPIRATION_DATE_TIME);
        return url;
    }

    @BeforeEach
    public void initTest() {
        url = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedUrl != null) {
            urlRepository.delete(insertedUrl);
            insertedUrl = null;
        }
    }

    @Test
    @Transactional
    void createUrl() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Url
        var returnedUrl = om.readValue(
            restUrlMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(url)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Url.class
        );

        // Validate the Url in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUrlUpdatableFieldsEquals(returnedUrl, getPersistedUrl(returnedUrl));

        insertedUrl = returnedUrl;
    }

    @Test
    @Transactional
    void createUrlWithExistingId() throws Exception {
        // Create the Url with an existing ID
        url.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUrlMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(url)))
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUrls() throws Exception {
        // Initialize the database
        insertedUrl = urlRepository.saveAndFlush(url);

        // Get all the urlList
        restUrlMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(url.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortUrl").value(hasItem(DEFAULT_SHORT_URL)))
            .andExpect(jsonPath("$.[*].fullUrl").value(hasItem(DEFAULT_FULL_URL)))
            .andExpect(jsonPath("$.[*].creationDateTime").value(hasItem(sameInstant(DEFAULT_CREATION_DATE_TIME))))
            .andExpect(jsonPath("$.[*].expirationDateTime").value(hasItem(sameInstant(DEFAULT_EXPIRATION_DATE_TIME))));
    }

    @Test
    @Transactional
    void getUrl() throws Exception {
        // Initialize the database
        insertedUrl = urlRepository.saveAndFlush(url);

        // Get the url
        restUrlMockMvc
            .perform(get(ENTITY_API_URL_ID, url.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(url.getId().intValue()))
            .andExpect(jsonPath("$.shortUrl").value(DEFAULT_SHORT_URL))
            .andExpect(jsonPath("$.fullUrl").value(DEFAULT_FULL_URL))
            .andExpect(jsonPath("$.creationDateTime").value(sameInstant(DEFAULT_CREATION_DATE_TIME)))
            .andExpect(jsonPath("$.expirationDateTime").value(sameInstant(DEFAULT_EXPIRATION_DATE_TIME)));
    }

    @Test
    @Transactional
    void getNonExistingUrl() throws Exception {
        // Get the url
        restUrlMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUrl() throws Exception {
        // Initialize the database
        insertedUrl = urlRepository.saveAndFlush(url);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the url
        Url updatedUrl = urlRepository.findById(url.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUrl are not directly saved in db
        em.detach(updatedUrl);
        updatedUrl
            .shortUrl(UPDATED_SHORT_URL)
            .fullUrl(UPDATED_FULL_URL)
            .creationDateTime(UPDATED_CREATION_DATE_TIME)
            .expirationDateTime(UPDATED_EXPIRATION_DATE_TIME);

        restUrlMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUrl.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(updatedUrl))
            )
            .andExpect(status().isOk());

        // Validate the Url in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUrlToMatchAllProperties(updatedUrl);
    }

    @Test
    @Transactional
    void putNonExistingUrl() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        url.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(put(ENTITY_API_URL_ID, url.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(url)))
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUrl() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        url.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(url))
            )
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUrl() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        url.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(url)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Url in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUrlWithPatch() throws Exception {
        // Initialize the database
        insertedUrl = urlRepository.saveAndFlush(url);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the url using partial update
        Url partialUpdatedUrl = new Url();
        partialUpdatedUrl.setId(url.getId());

        restUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUrl.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUrl))
            )
            .andExpect(status().isOk());

        // Validate the Url in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUrlUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUrl, url), getPersistedUrl(url));
    }

    @Test
    @Transactional
    void fullUpdateUrlWithPatch() throws Exception {
        // Initialize the database
        insertedUrl = urlRepository.saveAndFlush(url);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the url using partial update
        Url partialUpdatedUrl = new Url();
        partialUpdatedUrl.setId(url.getId());

        partialUpdatedUrl
            .shortUrl(UPDATED_SHORT_URL)
            .fullUrl(UPDATED_FULL_URL)
            .creationDateTime(UPDATED_CREATION_DATE_TIME)
            .expirationDateTime(UPDATED_EXPIRATION_DATE_TIME);

        restUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUrl.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUrl))
            )
            .andExpect(status().isOk());

        // Validate the Url in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUrlUpdatableFieldsEquals(partialUpdatedUrl, getPersistedUrl(partialUpdatedUrl));
    }

    @Test
    @Transactional
    void patchNonExistingUrl() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        url.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(patch(ENTITY_API_URL_ID, url.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(url)))
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUrl() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        url.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(url))
            )
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUrl() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        url.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(url)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Url in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUrl() throws Exception {
        // Initialize the database
        insertedUrl = urlRepository.saveAndFlush(url);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the url
        restUrlMockMvc.perform(delete(ENTITY_API_URL_ID, url.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return urlRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Url getPersistedUrl(Url url) {
        return urlRepository.findById(url.getId()).orElseThrow();
    }

    protected void assertPersistedUrlToMatchAllProperties(Url expectedUrl) {
        assertUrlAllPropertiesEquals(expectedUrl, getPersistedUrl(expectedUrl));
    }

    protected void assertPersistedUrlToMatchUpdatableProperties(Url expectedUrl) {
        assertUrlAllUpdatablePropertiesEquals(expectedUrl, getPersistedUrl(expectedUrl));
    }
}
