package com.codefactory.url_shortener.service;

import com.codefactory.url_shortener.exception.UrlNotFoundException;
import com.codefactory.url_shortener.model.UrlMapping;
import com.codefactory.url_shortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlMappingServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @InjectMocks
    private UrlMappingService service;

    @Test
    void shouldNotGenerateNewShortUrl_ifOriginalUrlExistsInDatabase() {
        UrlMapping existing = new UrlMapping(1L, "https://google.com", "abc123");

        when(repository.findByOriginalUrl("https://google.com"))
                .thenReturn(Optional.of(existing));

        String result = service.shortenUrl("https://google.com");

        assertEquals("abc123", result);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnOriginalUrl_whenShortUrlExists() {
        UrlMapping mapping = new UrlMapping(1L, "https://google.com", "abc123");
        when(repository.findByShortUrl("abc123")).thenReturn(Optional.of(mapping));

        String result = service.resolveUrl("abc123");

        assertEquals("https://google.com", result);
    }

    @Test
    void shouldThrowException_whenShortUrlNotFound() {
        when(repository.findByShortUrl("123")).thenReturn(Optional.empty());

        UrlNotFoundException ex = assertThrows(
                UrlNotFoundException.class,
                () -> service.resolveUrl("123")
        );

        assertEquals("Short URL not found: 123", ex.getMessage());
    }

    @Test
    void shouldGenerateNewShortUrl_whenOriginalUrlIsNew() {
        when(repository.findByOriginalUrl("https://new.com")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = service.shortenUrl("https://new.com");

        assertNotNull(result);
        verify(repository).save(any());
    }

}
