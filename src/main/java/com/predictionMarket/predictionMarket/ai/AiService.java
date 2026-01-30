package com.predictionMarket.predictionMarket.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
}
