package com.predictionMarket.predictionMarket.feed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record FeedPollingResponse(
    List<FeedEntry> entries,
    Instant pollTimestamp,
    Instant previousPollTimestamp,
    int entryCount
) {

    public FeedPollingResponse(List<FeedPollingResponse> responses){
        this(
                responses.stream()
                        .flatMap(r->r.entries.stream())
                        .toList(),
                responses.getFirst().pollTimestamp,
                responses.getFirst().previousPollTimestamp,
                responses.stream()
                        .mapToInt(r->r.entryCount)
                        .sum()
        );
    }
}
