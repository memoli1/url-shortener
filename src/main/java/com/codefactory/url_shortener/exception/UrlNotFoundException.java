package com.codefactory.url_shortener.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String shortUrl) {
        super("Short URL not found: " + shortUrl);
    }
}
