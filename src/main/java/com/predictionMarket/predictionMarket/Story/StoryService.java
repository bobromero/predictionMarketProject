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
    private final AiService aiService;

    @Autowired
    public StoryService(FeedPollingService feedPollingService, KalshiService kalshiService, AiService aiService) {
        this.feedPollingService = feedPollingService;
        this.kalshiService = kalshiService;
        this.aiService = aiService;
    }

    public List<KalshiStory> getStories(){
        //get rss data
        FeedPollingResponse response = feedPollingService.pollFeed();
        //put rss data into grok
        //log.info("stories prompt: {}", feedPollingService.getTitlesAsFlatString(response.entries()));
        //return result
        return kalshiService.getStoriesForKalshi(feedPollingService.getTitlesAsFlatString(response.entries()));
    }

    public String pollRssAndAskGrokForInsights(){
        // Doesn't work well, we get broken links
        FeedPollingResponse response = feedPollingService.pollFeed();
        String prompt = """
                given these articles, can you find prediction market events that are likely to change or be impacted, and you say how you think the markets will move
                summarize the articles in headlines
                 and add the links to the output as well
                
                Format:
                1. Politician is caught doing something!
                This is likely to imapact this world outcome
                Polymarket events that might be affected
                 1. Story headline
                    - Polymarket event link
                    Prediction: this will affect the market by ...
                
                Kalshi events that might be affected
                1. Story headline
                    - Kalshi event link
                    Prediction: this will affect the market by ...
                    
                Articles:
                %s
                """.formatted(response);
        return aiService.SimpleQuery(prompt);
    }
}
