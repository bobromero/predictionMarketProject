package com.predictionMarket.predictionMarket.kalshi;

public class KalshiEvent {
    String category;
    String seriesTicker;
    String seriesTitle;
    String eventTicker;
    String eventTitle;

    public KalshiEvent(String category, String seriesTicker, String seriesTitle, String eventTicker, String eventTitle) {
        this.category = category;
        this.seriesTicker = seriesTicker;
        this.seriesTitle = seriesTitle;
        this.eventTicker = eventTicker;
        this.eventTitle = eventTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeriesTicker() {
        return seriesTicker;
    }

    public void setSeriesTicker(String seriesTicker) {
        this.seriesTicker = seriesTicker;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public String getEventTicker() {
        return eventTicker;
    }

    public void setEventTicker(String eventTicker) {
        this.eventTicker = eventTicker;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventURL() {
        return "https://kalshi.com/markets/"+seriesTicker+"/"+seriesTitle.replaceAll(" ", "-")+"/"+eventTicker;
    }

}
