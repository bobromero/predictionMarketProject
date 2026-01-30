package com.predictionMarket.predictionMarket.Story;

import com.predictionMarket.predictionMarket.ai.AiService;
import com.predictionMarket.predictionMarket.feed.FeedPollingResponse;
import com.predictionMarket.predictionMarket.feed.FeedPollingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/stories")
public class StoryController {
    private static final Logger log = LoggerFactory.getLogger(StoryController.class);

    private FeedPollingService feedPollingService;
    private AiService aiService;

    @Autowired
    public StoryController(FeedPollingService feedPollingService, AiService aiService) {
        this.feedPollingService = feedPollingService;
        this.aiService = aiService;
    }

    @GetMapping
    public List<Story> getStories(){
        //get rss data
        FeedPollingResponse response = feedPollingService.pollFeed();
        //put rss data into grok
        log.info("stories prompt: {}", feedPollingService.getTitlesAsFlatString(response.entries()));
        List<Story> stories = aiService.getStories(feedPollingService.getTitlesAsFlatString(response.entries()));
        //return result
        return stories;
    }
}
