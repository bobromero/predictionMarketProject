package com.predictionMarket.predictionMarket.kalshi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/v1/kalshi")
public class KalshiController {

    private KalshiService kalshiService;

    @Autowired
    public void setKalshiService(KalshiService kalshiService){
        this.kalshiService=kalshiService;
    }


}
