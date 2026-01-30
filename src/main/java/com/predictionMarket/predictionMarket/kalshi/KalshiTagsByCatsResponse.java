package com.predictionMarket.predictionMarket.kalshi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class KalshiTagsByCatsResponse {
    @JsonProperty("tags_by_categories")
    private Map<String, List<String>> tags_by_categories;

    public Map<String, List<String>> getTags_by_categories() {
        return tags_by_categories;
    }
    public void setTags_by_categories(Map<String, List<String>> tags_by_categories) {
        this.tags_by_categories = tags_by_categories;
    }
}
