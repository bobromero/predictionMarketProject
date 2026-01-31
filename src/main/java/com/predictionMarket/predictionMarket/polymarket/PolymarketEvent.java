package com.predictionMarket.predictionMarket.polymarket;

import tools.jackson.databind.JsonNode;

public class PolymarketEvent {
    String id;
    String imageUrl;
    String title;
    JsonNode markets;

    public PolymarketEvent(String id, String imageUrl, String title, JsonNode markets) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.markets = markets;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JsonNode getMarkets() {
        return markets;
    }

    public void setMarkets(JsonNode markets) {
        this.markets = markets;
    }
}
