package com.notarius.shorturl.web.rest;

import com.notarius.shorturl.domain.Url;
import com.notarius.shorturl.repository.UrlRepository;
import com.notarius.shorturl.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.notarius.shorturl.domain.Url}.
 */
@RestController
@RequestMapping("/api/urls")
@Transactional
public class UrlResource {

    private static final Logger log = LoggerFactory.getLogger(UrlResource.class);

    private static final String ENTITY_NAME = "url";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UrlRepository urlRepository;

    public UrlResource(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /**
     * {@code POST  /urls} : Create a new url.
     *
     * @param url the url to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new url, or with status {@code 400 (Bad Request)} if the url has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Url> createUrl(@RequestBody Url url) throws URISyntaxException {
        log.debug("REST request to save Url : {}", url);
        if (url.getId() != null) {
            throw new BadRequestAlertException("A new url cannot already have an ID", ENTITY_NAME, "idexists");
        }
        url = urlRepository.save(url);
        return ResponseEntity.created(new URI("/api/urls/" + url.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, url.getId().toString()))
            .body(url);
    }

    /**
     * {@code PUT  /urls/:id} : Updates an existing url.
     *
     * @param id the id of the url to save.
     * @param url the url to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated url,
     * or with status {@code 400 (Bad Request)} if the url is not valid,
     * or with status {@code 500 (Internal Server Error)} if the url couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Url> updateUrl(@PathVariable(value = "id", required = false) final Long id, @RequestBody Url url)
        throws URISyntaxException {
        log.debug("REST request to update Url : {}, {}", id, url);
        if (url.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, url.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!urlRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        url = urlRepository.save(url);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, url.getId().toString()))
            .body(url);
    }

    /**
     * {@code PATCH  /urls/:id} : Partial updates given fields of an existing url, field will ignore if it is null
     *
     * @param id the id of the url to save.
     * @param url the url to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated url,
     * or with status {@code 400 (Bad Request)} if the url is not valid,
     * or with status {@code 404 (Not Found)} if the url is not found,
     * or with status {@code 500 (Internal Server Error)} if the url couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Url> partialUpdateUrl(@PathVariable(value = "id", required = false) final Long id, @RequestBody Url url)
        throws URISyntaxException {
        log.debug("REST request to partial update Url partially : {}, {}", id, url);
        if (url.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, url.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!urlRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Url> result = urlRepository
            .findById(url.getId())
            .map(existingUrl -> {
                if (url.getShortUrl() != null) {
                    existingUrl.setShortUrl(url.getShortUrl());
                }
                if (url.getFullUrl() != null) {
                    existingUrl.setFullUrl(url.getFullUrl());
                }
                if (url.getCreationDateTime() != null) {
                    existingUrl.setCreationDateTime(url.getCreationDateTime());
                }
                if (url.getExpirationDateTime() != null) {
                    existingUrl.setExpirationDateTime(url.getExpirationDateTime());
                }

                return existingUrl;
            })
            .map(urlRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, url.getId().toString())
        );
    }

    /**
     * {@code GET  /urls} : get all the urls.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of urls in body.
     */
    @GetMapping("")
    public List<Url> getAllUrls() {
        log.debug("REST request to get all Urls");
        return urlRepository.findAll();
    }

    /**
     * {@code GET  /urls/:id} : get the "id" url.
     *
     * @param id the id of the url to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the url, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Url> getUrl(@PathVariable("id") Long id) {
        log.debug("REST request to get Url : {}", id);
        Optional<Url> url = urlRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(url);
    }

    /**
     * {@code DELETE  /urls/:id} : delete the "id" url.
     *
     * @param id the id of the url to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable("id") Long id) {
        log.debug("REST request to delete Url : {}", id);
        urlRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
