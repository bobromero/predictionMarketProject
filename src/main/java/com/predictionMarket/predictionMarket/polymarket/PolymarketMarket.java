package com.predictionMarket.predictionMarket.polymarket;

import tools.jackson.databind.JsonNode;

public class PolymarketMarket {
    String question;
    String slug;
    float spread;
    JsonNode outcomePrices;
    JsonNode outcomes;
    String closed;

    public PolymarketMarket(String question, String slug, float spread, JsonNode outcomePrices, JsonNode outcomes, String closed) {
        this.question = question;
        this.slug = slug;
        this.spread = spread;
        this.outcomePrices = outcomePrices;
        this.outcomes = outcomes;
        this.closed = closed;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public float getSpread() {
        return spread;
    }

    public void setSpread(float spread) {
        this.spread = spread;
    }

    public JsonNode getOutcomePrices() {
        return outcomePrices;
    }

    public void setOutcomePrices(JsonNode outcomePrices) {
        this.outcomePrices = outcomePrices;
    }

    public JsonNode getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(JsonNode outcomes) {
        this.outcomes = outcomes;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }
}
