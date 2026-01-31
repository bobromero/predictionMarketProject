package com.predictionMarket.predictionMarket.Story;

import com.predictionMarket.predictionMarket.polymarket.PolymarketEvent;

import java.util.ArrayList;
import java.util.List;

public class PolymarketStory {
    String headline;
    String searchQ;
    List<PolymarketEvent> polymarketEvents = new ArrayList<>();


    public List<PolymarketEvent> getPolymarketEvents() {
        return polymarketEvents;
    }

    public void setPolymarketEvents(List<PolymarketEvent> polymarketEvents) {
        this.polymarketEvents = polymarketEvents;
    }

    public PolymarketStory(List<PolymarketEvent> polymarketEvents, String searchQ, String headline) {
        this.polymarketEvents = polymarketEvents;
        this.searchQ = searchQ;
        this.headline = headline;
    }

    public PolymarketStory() {

    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSearchQ() {
        return searchQ;
    }

    public void setSearchQ(String searchQ) {
        this.searchQ = searchQ;
    }
}
