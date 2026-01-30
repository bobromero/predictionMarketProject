package com.predictionMarket.predictionMarket.kalshi;

import com.predictionMarket.predictionMarket.Story.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class KalshiService {
    private static final Logger log = LoggerFactory.getLogger(KalshiService.class);
    private Map<String, List<String>> categoriesAndTags = new HashMap<>();
    private final RestClient restClient;

    public KalshiService(@Value("${kalshi.api.url}") String url){
        this.restClient = RestClient.builder().baseUrl(url).build();
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

    public List<String> getSeriesFromCategoryAndTags(String category, List<String> tags){
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/trade-api/v2/series")
                        .queryParam("category",category)
                        .queryParam("tags",tags)
                        .build()
                )
                .retrieve()
                .body(String.class);
        //I want to break down the json response into a list of
        //{"category":"","tags":[],"ticker":"","title":""}
        return List.of(response);

    }

    public List<KalshiEvent> getKalshiEvents(List<Story> stories){
        // we need to turn story into a kalshi event
        // we need to get 1 series for every story, and 1 event
        List<KalshiEvent> kalshiEvents = new ArrayList<>();
        for (Story story : stories) {
            List<String> allSeries = new ArrayList<>();
            //query kalshi series
            KalshiEvent kalshiEvent = new KalshiEvent();
            kalshiEvent.category = story.getCategory();
            kalshiEvent.tag= story.getTags().getFirst();

        }
        return kalshiEvents;
    }
}
