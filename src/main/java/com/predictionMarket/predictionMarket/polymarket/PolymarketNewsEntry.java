package com.predictionMarket.predictionMarket.polymarket;

import com.predictionMarket.predictionMarket.Story.PolymarketStory;

import java.util.List;

public class PolymarketNewsEntry {
    PolymarketStory story;
    String aiInsight;

    public PolymarketNewsEntry(PolymarketStory story) {
        this.story = story;
    }

    public PolymarketNewsEntry(PolymarketStory story, String aiInsight) {
        this.story = story;
        this.aiInsight = aiInsight;
    }

    public PolymarketStory getStory() {
        return story;
    }

    public void setStory(PolymarketStory story) {
        this.story = story;
    }

    public String getAiInsight() {
        return aiInsight;
    }

    public void setAiInsight(String aiInsight) {
        this.aiInsight = aiInsight;
    }
}
