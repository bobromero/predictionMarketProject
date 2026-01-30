package com.predictionMarket.predictionMarket.ai;

import com.predictionMarket.predictionMarket.kalshi.KalshiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class AiService {
    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final RestClient restClient;

    @Value("${grok.api.key}")
    private String apiKey;


    public AiService(@Value("${grok.api.url}") String url){
        this.restClient=RestClient.builder().baseUrl(url).build();
    }

    private KalshiService kalshiService;
    @Autowired
    public void setKalshiService(KalshiService kalshiService){
        this.kalshiService=kalshiService;
    }


    public String QueryGrok(String prompt){
        log.info("Querying grok for prediction market");
        Map<String,Object> request = Map.of(
                "model","grok-4-1-fast-reasoning",
                "input", List.of(
                        Map.of("role","system","content","return only 'true' or 'false' if the prompt relates to trump"),
                        Map.of("role","user","content",prompt)
                )
        );
        log.info(request.toString());
        AiResponse response = restClient.post()
                .uri("/v1/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(request)
                .retrieve()
                .body(AiResponse.class);
        log.info(response.toString());
        return response.getText();
    }
    public String getStories(String prompt){
        log.info("Getting stories for prediction market");
        Map<String, List<String>> categoriesAndTags = kalshiService.getCategoriesAndTags();
        log.info("Stories for prediction market: " + categoriesAndTags);
        log.info("Querying grok for prediction market");
        String systemPrompt ="""
            You are a classification assistant. Given a question, you must classify it into exactly one category and one or two relevant tags from that category.
            
            Available categories and tags:
            %s
            
            You MUST respond with ONLY valid JSON in this exact format, nothing else:
            {"category": "Category Name", "tags": ["tag1", "tag2"]}
            
            Rules:
            - Pick exactly ONE category
            - Pick ONE or TWO tags from that category only
            - No explanation, no extra text, just the JSON
            """.formatted(categoriesAndTags);
        Map<String,Object> request = Map.of(
                "model","grok-4-1-fast-reasoning",
                "input", List.of(
                        Map.of("role","system","content",systemPrompt),
                        Map.of("role","user","content",prompt)
                )
        );
        log.info(request.toString());
        AiResponse response = restClient.post()
                .uri("/v1/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(request)
                .retrieve()
                .body(AiResponse.class);

        log.info(response.toString());
        return response.getText();
    }
}
