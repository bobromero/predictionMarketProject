package com.predictionMarket.predictionMarket.kalshi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KalshiCategoryService {
    private static final Logger log = LoggerFactory.getLogger(KalshiCategoryService.class);

    private final RestClient restClient;
    private Map<String, List<String>> categoriesAndTags = new HashMap<>();

    public KalshiCategoryService(@Value("${kalshi.api.url}") String url) {
        this.restClient = RestClient.builder().baseUrl(url).build();
    }

    private KalshiTagsByCatsResponse queryCategoriesAndTags() {
        log.info("Querying Kalshi for categories and tags");
        try {
            KalshiTagsByCatsResponse response = restClient.get()
                    .uri("/trade-api/v2/search/tags_by_categories")
                    .retrieve()
                    .body(KalshiTagsByCatsResponse.class);
            assert response != null;
            return response;
        } catch (Exception e) {
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
}
