package com.predictionMarket.predictionMarket.feed;

import java.time.Instant;

public record FeedEntry(
    String title,
    String link,
    String description,
    Instant publishedDate,
    String author
) {}
