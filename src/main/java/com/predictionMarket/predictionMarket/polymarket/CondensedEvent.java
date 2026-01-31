package com.predictionMarket.predictionMarket.polymarket;

import java.util.ArrayList;
import java.util.List;

public class CondensedEvent {
    String title;
    List<CondensedMarket> condensedMarket;

    public CondensedEvent(PolymarketEvent polymarketEvent) {
        this.title = polymarketEvent.getTitle();
        this.condensedMarket = new ArrayList<CondensedMarket>();
        for (PolymarketMarket market: polymarketEvent.getMarkets()) {
            condensedMarket.add(new CondensedMarket(
                    market
            ));
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CondensedMarket> getCondensedMarket() {
        return condensedMarket;
    }

    public void setCondensedMarket(List<CondensedMarket> condensedMarket) {
        this.condensedMarket = condensedMarket;
    }
}
