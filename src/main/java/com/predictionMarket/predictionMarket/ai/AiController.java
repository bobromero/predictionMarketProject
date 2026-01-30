package com.predictionMarket.predictionMarket.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/v1/ai")
public class AiController {

    private AiService aiService;

    @Autowired
    public void setAiService(AiService aiService){
        this.aiService=aiService;
    }

    @GetMapping("/{pv}")
    public String getAi(@PathVariable String pv){
        return aiService.QueryGrok(pv);
    }
}
