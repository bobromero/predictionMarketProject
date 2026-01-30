package com.predictionMarket.predictionMarket.kalshi;

import java.util.List;

public class KalshiSeries {
    private String category;
    private List<String> tags;
    private String ticker;
    private String title;

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

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public KalshiSeries(String category, List<String> tags, String ticker, String title) {
        this.category = category;
        this.tags = tags;
        this.ticker = ticker;
        this.title = title;
    }
}
