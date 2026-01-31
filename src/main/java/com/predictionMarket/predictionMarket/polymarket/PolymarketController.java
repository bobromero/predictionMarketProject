package com.predictionMarket.predictionMarket.polymarket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/polymarket")
public class PolymarketController {
    private final PolymarketService polymarketService;

    @Autowired
    public PolymarketController(PolymarketService polymarketService) {
        this.polymarketService = polymarketService;
    }

    @GetMapping(path = "/events")
    public List<PolymarketNewsEntry> getEvents(){
        return polymarketService.pollRssAndGetPolymarketEvents();
    }

    @GetMapping(path = "/insights")
    public List<PolymarketNewsEntry> getInsights(){
        return polymarketService.getEventsAndAiInsight();
    }

}
