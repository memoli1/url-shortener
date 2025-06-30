package com.codefactory.url_shortener.controller;

import com.codefactory.url_shortener.dto.ShortenUrlRequest;
import com.codefactory.url_shortener.dto.ShortenUrlResponse;
import com.codefactory.url_shortener.dto.ResolveUrlResponse;
import com.codefactory.url_shortener.service.UrlMappingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
public class UrlShortenerController {

    private final UrlMappingService service;

    public UrlShortenerController(UrlMappingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        String shortUrl = service.shortenUrl(request.getOriginalUrl());
        return ResponseEntity.ok(new ShortenUrlResponse(shortUrl));
    }

    @GetMapping
    public ResolveUrlResponse resolve(@RequestParam String shortUrl) {
        String originalUrl = service.resolveUrl(shortUrl);
        return new ResolveUrlResponse(originalUrl);
    }
}
