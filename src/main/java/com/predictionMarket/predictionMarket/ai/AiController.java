package com.predictionMarket.predictionMarket.ai;

import com.predictionMarket.predictionMarket.Story.KalshiStory;
import com.predictionMarket.predictionMarket.kalshi.KalshiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/ai")
public class AiController {

    private KalshiService kalshiService;

    @Autowired
    public void setAiService(KalshiService kalshiService){
        this.kalshiService=kalshiService;
    }

    @PostMapping("/stories")
    public List<KalshiStory> getAiStoriesForKalshi(@RequestBody String articleTitles){

        return kalshiService.getStoriesForKalshi(articleTitles);
    }
}
