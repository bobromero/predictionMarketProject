package com.predictionMarket.predictionMarket.feed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedPollingService feedPollingService;

    public FeedController(FeedPollingService feedPollingService) {
        this.feedPollingService = feedPollingService;
    }

    @GetMapping("/poll")
    public FeedPollingResponse pollFeed() {
        return feedPollingService.pollFeed();
    }

    @GetMapping("/entries")
    public List<FeedEntry> getAllEntries() {
        return feedPollingService.getAllStoredEntries();
    }
}
