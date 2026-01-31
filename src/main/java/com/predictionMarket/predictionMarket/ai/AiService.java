package com.predictionMarket.predictionMarket.ai;

import com.predictionMarket.predictionMarket.Story.KalshiStory;
import com.predictionMarket.predictionMarket.kalshi.KalshiCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiService {
    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final RestClient restClient;
    private final KalshiCategoryService kalshiCategoryService;

    @Value("${grok.api.key}")
    private String apiKey;

    public AiService(@Value("${grok.api.url}") String url, KalshiCategoryService kalshiCategoryService) {
        this.restClient = RestClient.builder().baseUrl(url).build();
        this.kalshiCategoryService = kalshiCategoryService;
    }


    public String QueryGrok(String prompt){
        Map<String,Object> request = Map.of(
                "model", "grok-4-1-fast-reasoning",
                "input", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "text", Map.of(
                        "format", Map.of(
                                "type", "json_schema",
                                "name", "stories_response",
                                "strict", true,
                                "schema", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "stories", Map.of(
                                                        "type", "array",
                                                        "items", Map.of(
                                                                "type", "object",
                                                                "properties", Map.of(
                                                                        "headline", Map.of("type", "string"),
                                                                        "query", Map.of("type", "string")
                                                                ),
                                                                "required", List.of("headline", "query"),
                                                                "additionalProperties", false
                                                        )
                                                )
                                        ),
                                        "required", List.of("stories"),
                                        "additionalProperties", false
                                )
                        )
                )
        );
        AiResponse response = restClient.post()
                .uri("/v1/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(request)
                .retrieve()
                .body(AiResponse.class);
        assert response != null;
        log.info("Grok query response {}", response.getText());
        return response.getText();
    }





}
