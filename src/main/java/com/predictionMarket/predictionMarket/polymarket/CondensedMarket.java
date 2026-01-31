package com.predictionMarket.predictionMarket.polymarket;

import tools.jackson.databind.JsonNode;

public class CondensedMarket {
    String question;
    JsonNode outcomes;
    JsonNode outcomePrices;

    public CondensedMarket(PolymarketMarket market) {
        question = market.getQuestion();
        outcomes = market.getOutcomes();
        outcomePrices = market.getOutcomePrices();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public JsonNode getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(JsonNode outcomes) {
        this.outcomes = outcomes;
    }

    public JsonNode getOutcomePrices() {
        return outcomePrices;
    }

    public void setOutcomePrices(JsonNode outcomePrices) {
        this.outcomePrices = outcomePrices;
    }
}
