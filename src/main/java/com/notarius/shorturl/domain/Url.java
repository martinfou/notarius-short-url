package com.notarius.shorturl.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Url.
 */
@Entity
@Table(name = "url")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Url implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "short_url")
    private String shortUrl;

    @Column(name = "full_url")
    private String fullUrl;

    @Column(name = "creation_date_time")
    private ZonedDateTime creationDateTime;

    @Column(name = "expiration_date_time")
    private ZonedDateTime expirationDateTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Url id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    public Url shortUrl(String shortUrl) {
        this.setShortUrl(shortUrl);
        return this;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getFullUrl() {
        return this.fullUrl;
    }

    public Url fullUrl(String fullUrl) {
        this.setFullUrl(fullUrl);
        return this;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public ZonedDateTime getCreationDateTime() {
        return this.creationDateTime;
    }

    public Url creationDateTime(ZonedDateTime creationDateTime) {
        this.setCreationDateTime(creationDateTime);
        return this;
    }

    public void setCreationDateTime(ZonedDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public ZonedDateTime getExpirationDateTime() {
        return this.expirationDateTime;
    }

    public Url expirationDateTime(ZonedDateTime expirationDateTime) {
        this.setExpirationDateTime(expirationDateTime);
        return this;
    }

    public void setExpirationDateTime(ZonedDateTime expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Url)) {
            return false;
        }
        return getId() != null && getId().equals(((Url) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Url{" +
            "id=" + getId() +
            ", shortUrl='" + getShortUrl() + "'" +
            ", fullUrl='" + getFullUrl() + "'" +
            ", creationDateTime='" + getCreationDateTime() + "'" +
            ", expirationDateTime='" + getExpirationDateTime() + "'" +
            "}";
    }
}
