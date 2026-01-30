package com.predictionMarket.predictionMarket.ai;

import com.predictionMarket.predictionMarket.Story.Story;
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


    public String QueryGrok(Map<String, List<String>> categoriesAndTags, String prompt){
        log.info("prompt={}",prompt);
        String systemPrompt ="""
            You MUST respond with ONLY valid JSON in this exact format, nothing else:
            {"stories":["headline":"Story Headline","category": "Category Name", "tags": ["tag1", "tag2"]]}
            You are a classification assistant ONLY RETURN JSON. Given a list of titles, classify them into "stories" where each story has a 1 sentence headline categorizing titles that are likely related to the same event.
            You will also give each story exactly one category and one or two relevant tags from that category.
            Rules:
            - Keep story headlines brief ONE sentence
            - Pick exactly ONE category
            - Pick ONE or TWO tags from that category only
            - No explanation, no extra text, just the JSON
            - ONLY VALID JSON, no other words/ tokens
            - YOUR RESPONSE MUST ONLY BE VALID JSON
            Title(s):
            %s
            
            Available categories and tags:
            %s
            """.formatted(prompt,categoriesAndTags);
        Map<String,Object> request = Map.of(
                "model","grok-4-1-fast-reasoning",
                "input", List.of(
                        Map.of("role","system","content",systemPrompt)
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



    public String getGrokStories(String prompt){
        log.info("Getting stories for prediction market");
        Map<String, List<String>> categoriesAndTags = kalshiCategoryService.getCategoriesAndTags();

        return QueryGrok(categoriesAndTags, prompt);
    }
    public List<Story> getStories(String prompt){
        log.info("Getting stories for prediction market");
        String grokStories = getGrokStories(prompt);
        log.info("Grok stories {}", grokStories);
        List<Story> stories = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(grokStories);
        try {
            for (JsonNode storiesNode : json.get("stories")) {
                Story story = new Story();
                story.setHeadline(storiesNode.get("headline").asText());
                story.setCategory(storiesNode.get("category").asText());

                List<String> tagsList = new ArrayList<>();
                for (JsonNode tag: storiesNode.get("tags")){
                    tagsList.add(tag.asText());
                }
                story.setTags(tagsList);

                stories.add(story);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to parse json {}",json);
            log.error("Error getting stories for prediction market",e);
        }
        log.info(stories.toString());
        return stories;
    }
}
