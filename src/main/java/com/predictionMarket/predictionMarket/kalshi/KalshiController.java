package com.predictionMarket.predictionMarket.kalshi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/api/v1/kalshi")
public class KalshiController {

    private KalshiService kalshiService;

    @Autowired
    public void setKalshiService(KalshiService kalshiService){
        this.kalshiService=kalshiService;
    }


    @GetMapping(path="/series")
    public List<String> getSeries(@RequestParam String category,@RequestParam String tag){
        return kalshiService.getSeriesFromCategoryAndTags(category,List.of(tag));
    }
}
