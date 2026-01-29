package com.predictionMarket.predictionMarket.feed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FeedScheduler {

    private static final Logger log = LoggerFactory.getLogger(FeedScheduler.class);

    private final FeedPollingService feedPollingService;

    public FeedScheduler(FeedPollingService feedPollingService) {
        this.feedPollingService = feedPollingService;
    }

    @Scheduled(fixedRateString = "${feed.poll.interval:300000}")
    public void scheduledPoll() {
        log.info("Starting scheduled feed poll");
        try {
            FeedPollingResponse response = feedPollingService.pollFeed();
            log.info("Scheduled poll complete: {} entries found", response.entryCount());
        } catch (Exception e) {
            log.error("Scheduled poll failed: {}", e.getMessage(), e);
        }
    }
}
