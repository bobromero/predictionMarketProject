package com.predictionMarket.predictionMarket.Story;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/stories")
public class StoryController {
    private final StoryService storyService;

    @Autowired
    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping
    public List<KalshiStory> getStories(){
        return storyService.getStories();
    }

    @GetMapping(path="/insight")
    public String getInsight(){
        return storyService.pollRssAndAskGrokForInsights();
    }
}
