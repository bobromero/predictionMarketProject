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
            List<PolymarketMarket>markets = new ArrayList<>();
            for (JsonNode market : event.get("markets")){
                if (market.get("closed").asBoolean()){
                    continue;
                }
                markets.add(new PolymarketMarket(
                        market.get("question").asText(),
                        market.get("slug").asText(),
                        market.get("spread").asFloat(),
                        market.get("outcomePrices"),
                        market.get("outcomes"),
                        market.get("closed").asText()
                ));
            }
            polymarketEvents.add(new PolymarketEvent(
                    event.get("id").asText(),
                    event.get("image").asText(),
                    event.get("slug").asText(),
                    event.get("title").asText(),
                    event.get("closed").asText(),
                    markets
            ));
        }
        return polymarketEvents;
    }

    private List<PolymarketStory> getStories(String articleTitles){
        //give AI prompt and return stories
        log.info("querying grok for stories");
        String prompt = """
            You are a classification assistant ONLY RETURN JSON. Given a list of titles, classify them into "stories" where each story has a 1 sentence headline categorizing titles that are likely related to the same event.
            Also add a query for each story 1 - 5 key words as it needs to be concise
            Rules:
            - Keep story headlines brief ONE sentence
            - YOUR RESPONSE MUST ONLY BE VALID JSON IN THIS FORMAT
            {"stories":[{"headline":"Story Headline","query": "encapsulate the story in 1-5 key words"}]}
            
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
                story.setPolymarketEvents(filterClosedEvents(parsePolymarketEvents(json)));

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

    private List<PolymarketEvent> filterClosedEvents(List<PolymarketEvent> polymarketEvents) {
        List<PolymarketEvent> filteredEvents = new ArrayList<>();
        for (PolymarketEvent polymarketEvent : polymarketEvents) {
            if (polymarketEvent.closed.equals("false")){
                filteredEvents.add(polymarketEvent);
            }
        }
        return filteredEvents;
    }

    public List<PolymarketNewsEntry> pollRssAndGetPolymarketEvents(){
        FeedPollingResponse response = feedPollingService.pollFeed();
        log.info("getting stories");
        List<PolymarketStory> stories = getStories(feedPollingService.getTitlesAsFlatString(response.entries()));
        log.info("Got {} stories", stories.size());
        List<PolymarketNewsEntry> polymarketNewsEntries = new ArrayList<>();
        for (PolymarketStory story : stories) {
            polymarketNewsEntries.add(new PolymarketNewsEntry(
                    story
            ));
        }
        return polymarketNewsEntries;
    }



}
