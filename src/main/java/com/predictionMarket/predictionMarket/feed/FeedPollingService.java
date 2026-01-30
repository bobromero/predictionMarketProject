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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FeedPollingService {

    private static final Logger log = LoggerFactory.getLogger(FeedPollingService.class);

    private final List<String> rssFeeds;
    private final AtomicReference<Instant> lastPollTimestamp = new AtomicReference<>();
    private final Set<String> seenLinks = ConcurrentHashMap.newKeySet();
    private final List<FeedEntry> storedEntries = new CopyOnWriteArrayList<>();

    public FeedPollingService(
            @Value("${feed.google.news.url}") String googleFeedUrl,
            @Value("${feed.fox.news.us.url}") String FoxUSFeedUrl,
            @Value("${feed.fox.news.world.url}") String FoxWorldFeedUrl,
            @Value("${feed.initial.lookback.hours:24}") int initialLookbackHours) {
        this.rssFeeds = new ArrayList<String>();
        this.rssFeeds.add(googleFeedUrl);
        this.rssFeeds.add(FoxUSFeedUrl);
        this.rssFeeds.add(FoxWorldFeedUrl);
    }

    public FeedPollingResponse pollFeed() {
        Instant now = Instant.now();
        Instant previousPoll = lastPollTimestamp.get();

        List<FeedPollingResponse> pollingResponses = new ArrayList<>();

        for (String feedUrl : rssFeeds) {
            log.info("Polling feed: {} (tracking {} seen links)", feedUrl, seenLinks.size());

            try {
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed;

                try (XmlReader reader = new XmlReader(URI.create(feedUrl).toURL())) {
                    feed = input.build(reader);
                }

                List<FeedEntry> entries = feed.getEntries().stream()
                        .filter(entry -> entry.getLink() != null && !seenLinks.contains(entry.getLink()))
                        .map(this::toFeedEntry)
                        .toList();

                // Add new links to seen set
                entries.forEach(entry -> seenLinks.add(entry.link()));

                storedEntries.addAll(entries);
                lastPollTimestamp.set(now);

                log.info("Poll complete. Found {} new entries, total stored: {}", entries.size(), storedEntries.size());

                pollingResponses.add( new FeedPollingResponse(
                        entries,
                        now,
                        previousPoll,
                        entries.size()
                ));

            } catch (Exception e) {
                log.error("Error polling feed: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to poll RSS feed", e);
            }
        }
        return new FeedPollingResponse(pollingResponses);
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
            entry.getPublishedDate() != null ? entry.getPublishedDate().toInstant() : null,
            entry.getAuthor()
        );
    }
}
