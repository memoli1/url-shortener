package com.codefactory.url_shortener.repository;

import com.codefactory.url_shortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortUrl(String shortUrl);

    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
}
