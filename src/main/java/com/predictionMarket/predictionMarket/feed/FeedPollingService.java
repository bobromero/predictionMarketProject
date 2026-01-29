package com.predictionMarket.predictionMarket.feed;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FeedPollingService {

    private static final Logger log = LoggerFactory.getLogger(FeedPollingService.class);

    private final String feedUrl;
    private final int initialLookbackHours;
    private final AtomicReference<Instant> lastPollTimestamp = new AtomicReference<>();
    private final List<FeedEntry> storedEntries = new CopyOnWriteArrayList<>();

    public FeedPollingService(
            @Value("${feed.google.news.url}") String feedUrl,
            @Value("${feed.initial.lookback.hours:24}") int initialLookbackHours) {
        this.feedUrl = feedUrl;
        this.initialLookbackHours = initialLookbackHours;
    }

    public FeedPollingResponse pollFeed() {
        Instant now = Instant.now();
        Instant previousPoll = lastPollTimestamp.get();

        Instant filterCutoff = previousPoll != null
            ? previousPoll
            : now.minus(initialLookbackHours, ChronoUnit.HOURS);

        log.info("Polling feed: {} (filtering entries after {})", feedUrl, filterCutoff);

        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed;

            try (XmlReader reader = new XmlReader(URI.create(feedUrl).toURL())) {
                feed = input.build(reader);
            }

            List<FeedEntry> entries = feed.getEntries().stream()
                .filter(entry -> {
                    Date pubDate = entry.getPublishedDate();
                    return pubDate != null && pubDate.toInstant().isAfter(filterCutoff);
                })
                .map(this::toFeedEntry)
                .toList();

            storedEntries.addAll(entries);
            lastPollTimestamp.set(now);

            log.info("Poll complete. Found {} new entries, total stored: {}", entries.size(), storedEntries.size());

            return new FeedPollingResponse(
                entries,
                now,
                previousPoll,
                entries.size()
            );

        } catch (Exception e) {
            log.error("Error polling feed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to poll RSS feed", e);
        }
    }

    public Instant getLastPollTimestamp() {
        return lastPollTimestamp.get();
    }

    public List<FeedEntry> getAllStoredEntries() {
        return Collections.unmodifiableList(new ArrayList<>(storedEntries));
    }

    public int getStoredEntryCount() {
        return storedEntries.size();
    }

    private FeedEntry toFeedEntry(SyndEntry entry) {
        return new FeedEntry(
            entry.getTitle(),
            entry.getLink(),
            entry.getDescription() != null ? entry.getDescription().getValue() : null,
            entry.getPublishedDate() != null ? entry.getPublishedDate().toInstant() : null,
            entry.getAuthor()
        );
    }
}
