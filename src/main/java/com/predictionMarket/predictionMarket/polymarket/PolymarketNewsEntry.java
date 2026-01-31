package com.predictionMarket.predictionMarket.polymarket;

import com.predictionMarket.predictionMarket.Story.PolymarketStory;

import java.util.List;

public class PolymarketNewsEntry {
    PolymarketStory story;

    public PolymarketNewsEntry(PolymarketStory story) {
        this.story = story;
    }

    public PolymarketStory getStory() {
        return story;
    }

    public void setStory(PolymarketStory story) {
        this.story = story;
    }
}
