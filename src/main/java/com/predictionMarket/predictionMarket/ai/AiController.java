package com.predictionMarket.predictionMarket.ai;

import com.predictionMarket.predictionMarket.Story.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/ai")
public class AiController {

    private AiService aiService;

    @Autowired
    public void setAiService(AiService aiService){
        this.aiService=aiService;
    }

    @PostMapping("/stories")
    public List<Story> getAi(@RequestBody String articleTitles){

        return aiService.getStories(articleTitles);
    }
}
