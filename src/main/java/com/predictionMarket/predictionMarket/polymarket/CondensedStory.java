package com.predictionMarket.predictionMarket.polymarket;

import com.predictionMarket.predictionMarket.Story.PolymarketStory;

import java.util.ArrayList;
import java.util.List;

public class CondensedStory {
    String headline;
    String searchQ;
    List<CondensedEvent> condensedEvents;

    public CondensedStory(PolymarketStory polymarketStory){
        this.headline = polymarketStory.getHeadline();
        this.searchQ = polymarketStory.getSearchQ();
        this.condensedEvents = new ArrayList<>();
        for (PolymarketEvent event: polymarketStory.getPolymarketEvents()){
            condensedEvents.add(new CondensedEvent(event));
        }
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

    public List<CondensedEvent> getCondensedEvents() {
        return condensedEvents;
    }

    public void setCondensedEvents(List<CondensedEvent> condensedEvents) {
        this.condensedEvents = condensedEvents;
    }


}
