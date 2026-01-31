package com.predictionMarket.predictionMarket.polymarket;

public class PolymarketEvent {
    String id;
    String imageUrl;
    String slug;
    String eventUrl;
    String title;
    String closed;




    public PolymarketEvent(String id, String imageUrl, String slug, String title,  String closed) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.slug = slug;
        this.eventUrl = "https://polymarket.com/event/"+slug;
        this.title = title;
        this.closed = closed;
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

    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getEventUrl() {
        return this.eventUrl;
    }
    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }
}
