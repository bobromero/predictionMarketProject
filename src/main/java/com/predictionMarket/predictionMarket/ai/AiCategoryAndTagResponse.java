package com.predictionMarket.predictionMarket.ai;

import java.util.List;

public class AiCategoryAndTagResponse {
    private String category;
    private List<String> tags;
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
