package com.predictionMarket.predictionMarket.Story;

import com.predictionMarket.predictionMarket.ai.AiService;
import com.predictionMarket.predictionMarket.feed.FeedPollingResponse;
import com.predictionMarket.predictionMarket.feed.FeedPollingService;
import com.predictionMarket.predictionMarket.kalshi.KalshiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryService {
    private static final Logger log = LoggerFactory.getLogger(StoryController.class);

    private final FeedPollingService feedPollingService;
    private final KalshiService kalshiService;

    @Autowired
    public StoryService(FeedPollingService feedPollingService, KalshiService kalshiService) {
        this.feedPollingService = feedPollingService;
        this.kalshiService = kalshiService;
    }

    public List<KalshiStory> getStories(){
        //get rss data
        FeedPollingResponse response = feedPollingService.pollFeed();
        //put rss data into grok
        //log.info("stories prompt: {}", feedPollingService.getTitlesAsFlatString(response.entries()));
        //return result
        return kalshiService.getStoriesForKalshi(feedPollingService.getTitlesAsFlatString(response.entries()));
    }
}
