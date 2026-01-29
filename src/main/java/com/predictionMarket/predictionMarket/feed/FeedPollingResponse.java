package com.predictionMarket.predictionMarket.feed;

import java.time.Instant;
import java.util.List;

public record FeedPollingResponse(
    List<FeedEntry> entries,
    Instant pollTimestamp,
    Instant previousPollTimestamp,
    int entryCount
) {}
