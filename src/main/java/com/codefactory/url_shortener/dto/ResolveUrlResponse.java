package com.codefactory.url_shortener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResolveUrlResponse {
    private String originalUrl;
}
