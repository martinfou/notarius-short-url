package com.notarius.shorturl.repository;

import com.notarius.shorturl.domain.Url;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Url entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortUrl(String shortUrl);
    Boolean existsByShortUrl(String shortUrl);
}
