package me.gatogamer.nekotops.top;

import lombok.Getter;
import me.gatogamer.midnight.commons.io.FileIO;
import me.gatogamer.nekotops.NekoTops;

import java.io.File;
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
public class TopManager {
    private final Map<String, TopFetcher> tops = new ConcurrentHashMap<>();

    public TopManager(NekoTops nekoTops) {
        nekoTops.getDataFolder().mkdir();
        File topFolder = new File(nekoTops.getDataFolder(), "tops");
        topFolder.mkdir();
        for (File file : topFolder.listFiles()) {
            TopFetcher topFetcher = nekoTops.getMidnightImpl().getGson().fromJson(FileIO.readFile(file), TopFetcher.class);
            tops.put(topFetcher.getName(), topFetcher);
        }
    }
}