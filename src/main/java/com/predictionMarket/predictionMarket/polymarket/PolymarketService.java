package com.predictionMarket.predictionMarket.polymarket;

import com.predictionMarket.predictionMarket.Story.PolymarketStory;
import com.predictionMarket.predictionMarket.ai.AiService;
import com.predictionMarket.predictionMarket.feed.FeedPollingResponse;
import com.predictionMarket.predictionMarket.feed.FeedPollingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class PolymarketService {
    public static final Logger log = LoggerFactory.getLogger(PolymarketService.class);

    private final RestClient restClient;
    private final FeedPollingService feedPollingService;
    private final AiService aiService;
    @Autowired
    public PolymarketService(@Value("${polymarket.api.url}") String url, FeedPollingService feedPollingService, AiService aiService) {
        this.restClient = RestClient.builder().baseUrl(url).build();
        this.feedPollingService = feedPollingService;
        this.aiService = aiService;
    }

    private JsonNode queryPolymarketSearch(String query){
        JsonNode response = null;
        log.info("querying polymarket search with {}", query);
        try {
            response = restClient.get()
                    .uri(builder -> builder
                            .path("/public-search")
                            .queryParam("q", query)
                            .queryParam("keep_closed_markets", "0")
                            .queryParam("optimized", "true")
                            .build()
                    )
                    .retrieve()
                    .body(JsonNode.class);
        }
        catch (Exception e){
            log.error("error querying polymarket: {}",e.getMessage());
        }
        if (response == null){
            response = JsonNodeFactory.instance.nullNode();
        }
        return response;
    }

    private List<PolymarketEvent> parsePolymarketEvents(JsonNode response){
        List<PolymarketEvent> polymarketEvents = new ArrayList<>();
        for (JsonNode event : response.get("events")){
            polymarketEvents.add(new PolymarketEvent(
                    event.get("id").asText(),
                    event.get("image").asText(),
                    event.get("title").asText(),
                    event
            ));
        }
        return polymarketEvents;
    }

    private List<PolymarketStory> getStories(String articleTitles){
        //give AI prompt and return stories
        log.info("querying grok for stories");
        String prompt = """
            You MUST respond with ONLY valid JSON in this exact format, nothing else:
            {"stories":[{"headline":"Story Headline","query": "max 1 sentence query"}]}
            You are a classification assistant ONLY RETURN JSON. Given a list of titles, classify them into "stories" where each story has a 1 sentence headline categorizing titles that are likely related to the same event.
            Also add a query for each story to find events that might happen in the future because of this story and add it to the json.
            Rules:
            - Keep story headlines brief ONE sentence
            - No explanation, no extra text, just the JSON
            - ONLY VALID JSON, no other words/ tokens
            - YOUR RESPONSE MUST ONLY BE VALID JSON
            Title(s):
            %s
            """.formatted(articleTitles);
        String grokStories = aiService.QueryGrok(prompt);
        //this will return my JSON
        log.info("Grok stories {}", grokStories);
        List<PolymarketStory> stories = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode storyJson = mapper.readTree(grokStories);
        try {
            for (JsonNode storiesNode : storyJson.get("stories")) {
                PolymarketStory story = new PolymarketStory();
                story.setHeadline(storiesNode.get("headline").asText());
                story.setSearchQ(storiesNode.get("query").asText());
                //query Polymarket search with query as the parameter
                JsonNode json = queryPolymarketSearch(story.getSearchQ());
                //map JSON onto event and add
                log.info("adding event {}", json);
                story.setPolymarketEvents(parsePolymarketEvents(json));

                stories.add(story);
            }
        }
        catch (Exception e) {
            log.error("Failed to parse json {}",storyJson);
            log.error("Error getting stories for prediction market",e);
        }
        log.info(stories.toString());
        return stories;
    }


    public List<PolymarketEvent> pollRssAndGetPolymarketEvents(){
        FeedPollingResponse response = feedPollingService.pollFeed();
        log.info("getting stories");
        List<PolymarketStory> stories = getStories(feedPollingService.getTitlesAsFlatString(response.entries()));
        log.info("Got {} stories", stories.size());
        return stories.stream().flatMap(story -> story.getPolymarketEvents().stream()).toList();
    }
}
