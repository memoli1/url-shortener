package com.codefactory.url_shortener.service;

import com.codefactory.url_shortener.exception.UrlNotFoundException;
import com.codefactory.url_shortener.model.UrlMapping;
import com.codefactory.url_shortener.repository.UrlMappingRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlMappingService {

    @Value("${shorturl.length}")
    private int shortUrlLength;

    private final UrlMappingRepository repository;

    public UrlMappingService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String shortenUrl(String originalUrl) {
        return repository.findByOriginalUrl(originalUrl)
                .map(UrlMapping::getShortUrl)
                .orElseGet(() -> createAndSaveMapping(originalUrl));
    }

    @Transactional(readOnly = true)
    public String resolveUrl(String shortUrl) {
        return repository.findByShortUrl(shortUrl)
                .map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new UrlNotFoundException(shortUrl));
    }

    private String generateShortUrl() {
        return RandomStringUtils.randomAlphanumeric(shortUrlLength);
    }

    private String createAndSaveMapping(String originalUrl) {
        String shortUrl = generateShortUrl();

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl(originalUrl)
                .shortUrl(shortUrl)
                .build();

        repository.save(mapping);
        return shortUrl;
    }
}
