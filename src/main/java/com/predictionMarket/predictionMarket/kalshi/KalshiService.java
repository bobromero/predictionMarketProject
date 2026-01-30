package com.predictionMarket.predictionMarket.kalshi;

import com.predictionMarket.predictionMarket.Story.Story;
import com.predictionMarket.predictionMarket.Story.StoryService;
import com.predictionMarket.predictionMarket.ai.AiService;
import com.predictionMarket.predictionMarket.feed.FeedPollingResponse;
import com.predictionMarket.predictionMarket.feed.FeedPollingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Service
public class KalshiService {
    private Map<String, List<String>> categoriesAndTags = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(KalshiService.class);

    private final RestClient restClient;
    private final FeedPollingService feedPollingService;
    private final AiService aiService;

    @Autowired
    public KalshiService(@Value("${kalshi.api.url}") String url, FeedPollingService feedPollingService, AiService aiService) {
        this.restClient = RestClient.builder().baseUrl(url).build();
        this.feedPollingService = feedPollingService;
        this.aiService = aiService;
    }

    private KalshiTagsByCatsResponse queryCategoriesAndTags(){
        log.info("Querying Kalshi for categories and tags");
        try{
            KalshiTagsByCatsResponse response = restClient.get()
                    .uri("/trade-api/v2/search/tags_by_categories")
                    .retrieve()
                    .body(KalshiTagsByCatsResponse.class);
            assert response != null;
            //log.info(response.getTags_by_categories().toString());

            return response;
        }catch(Exception e){
            log.error("Error querying Kalshi:", e);
        }
        return new KalshiTagsByCatsResponse();
    }

    public Map<String, List<String>> getCategoriesAndTags() {
        if (categoriesAndTags.isEmpty()) {
            KalshiTagsByCatsResponse response = queryCategoriesAndTags();
            categoriesAndTags = response.getTags_by_categories();
        }
        return categoriesAndTags;
    }

    public List<KalshiSeries> queryKalshiSeries(String category, String tags){
        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/trade-api/v2/series")
                        .queryParam("category",category)
                        .queryParam("tags",tags)
                        .build()
                )
                .retrieve()
                .body(JsonNode.class);

        //I want to break down the json response into a list of
        //{"category":"","tags":[],"ticker":"","title":""}

        List<KalshiSeries> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (JsonNode series : response.get("series")) {
            result.add(new KalshiSeries(
                    series.get("category").asText(),
                    mapper.convertValue(series.get("tags"), new TypeReference<List<String>>() {}),
                    series.get("ticker").asText(),
                    series.get("title").asText()
            ));
        }
        return result;

    }

    private List<KalshiEvent> queryKalshiEvents(String seriesTicker, String seriesTitle){
        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("trade-api/v2/events")
                        .queryParam("status","open")
                        .queryParam("series_ticker",seriesTicker.toUpperCase())
                        .build()
                )
                .retrieve()
                .body(JsonNode.class);
        List<KalshiEvent> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (JsonNode event : response.get("events")) {
            result.add(new KalshiEvent(
                    event.get("category").asText(),
                    event.get("series_ticker").asText(),
                    seriesTitle,
                    event.get("event_ticker").asText(),
                    event.get("title").asText()
            ));
        }
        return result;
    }

    private List<KalshiEvent> getKalshiEventsFromStories(List<Story> stories){
        // we need to turn story into a kalshi event
        // we need to get 1 series for every story, and 1 event
        List<KalshiEvent> kalshiEvents = new ArrayList<>();
        for (Story story : stories) {
            //query kalshi series
            List<KalshiSeries> kalshiSeries = queryKalshiSeries(story.getCategory(), String.join(",", story.getTags()));
            for (KalshiSeries series : kalshiSeries) {
                //query kalshi event
                kalshiEvents = Stream.of(kalshiEvents,queryKalshiEvents(series.getTicker(), series.getTitle()))
                        .flatMap(List::stream)
                        .toList();
            }


        }
        return kalshiEvents;
    }
    public List<KalshiEvent> pollRssAndGetKalshiEvents(){
            FeedPollingResponse response = feedPollingService.pollFeed();
            List<Story> stories = aiService.getStories(feedPollingService.getTitlesAsFlatString(response.entries()));
            List<KalshiEvent> events = getKalshiEventsFromStories(stories);
            return events;
    }

}
