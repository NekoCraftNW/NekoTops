package me.gatogamer.nekotops.hologram;

import lombok.Getter;
import me.gatogamer.nekotops.top.TopManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@Getter
public class HologramManager {
    private final Map<String, TopHologram> tops = new ConcurrentHashMap<>();

    public HologramManager(TopManager topManager) {
        topManager.getTops().forEach((s, topFetcher) -> {
            tops.put(s, new TopHologram(s, topFetcher));
        });
    }
}