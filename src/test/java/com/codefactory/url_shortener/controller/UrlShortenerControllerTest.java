package com.codefactory.url_shortener.controller;

import com.codefactory.url_shortener.repository.UrlMappingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlMappingRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldShortenAndResolveUrl() throws Exception {
        // POST shorten
        MvcResult postResult = mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "originalUrl": "https://www.google.com"
                                    }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String shortUrl = new ObjectMapper()
                .readTree(postResult.getResponse().getContentAsString())
                .get("shortUrl")
                .asText();

        // GET resolve
        mockMvc.perform(get("/api/urls")
                        .param("shortUrl", shortUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.com"));
    }
}
