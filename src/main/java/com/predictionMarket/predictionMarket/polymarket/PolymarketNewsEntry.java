package com.predictionMarket.predictionMarket.polymarket;

import com.predictionMarket.predictionMarket.Story.PolymarketStory;

import java.util.List;

public class PolymarketNewsEntry {
    PolymarketStory story;
    List<PolymarketEvent> events;

    public PolymarketNewsEntry(PolymarketStory story, List<PolymarketEvent> events) {
        this.story = story;
        this.events = events;
    }

    public PolymarketStory getStory() {
        return story;
    }

    public void setStory(PolymarketStory story) {
        this.story = story;
    }

    public List<PolymarketEvent> getEvents() {
        return events;
    }

    public void setEvents(List<PolymarketEvent> events) {
        this.events = events;
    }
}
