package com.predictionMarket.predictionMarket.kalshi;

import com.predictionMarket.predictionMarket.Story.Story;
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
import tools.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Service
public class KalshiService {
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

    public List<KalshiSeries> queryKalshiSeries(String category, String tags){
        JsonNode response = null;
        try{
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/trade-api/v2/series")
                            .queryParam("category",category)
                            .queryParam("tags",tags)
                            .build()
                    )
                    .retrieve()
                    .body(JsonNode.class);
            Thread.sleep(200);
        }catch(Exception e){
            log.error(e.getMessage());
        }
        //I want to break down the json response into a list of
        //{"category":"","tags":[],"ticker":"","title":""}

        List<KalshiSeries> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        if(response == null){
            log.error("series request null");
            return result;
        }
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
        JsonNode response=null;
        try {
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("trade-api/v2/events")
                            .queryParam("status", "open")
                            .queryParam("series_ticker", seriesTicker.toUpperCase())
                            .build()
                    )
                    .retrieve()
                    .body(JsonNode.class);
            Thread.sleep(200);
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        //log.info("response {} ",response);
        List<KalshiEvent> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        if(response == null){
            log.error("events request null");
            return result;
        }
        for (JsonNode event : response.get("events")) {
            //log.info(event.get("title").asText());
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
            log.info("getting events for {} series",kalshiSeries.size());
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
            log.info("Got {} stories", stories.size());
            List<KalshiEvent> events = getKalshiEventsFromStories(stories);
            return events;
    }

}
