package me.gatogamer.nekotops.hologram;

import lombok.Getter;
import me.gatogamer.midnight.spigot.Midnight;
import me.gatogamer.midnight.spigot.utils.MessagesUtils;
import me.gatogamer.nekotops.NekoTops;
import me.gatogamer.nekotops.top.TopData;
import me.gatogamer.nekotops.top.TopFetcher;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@Getter
public class TopHologram {
    private final String name;
    private final TopFetcher topFetcher;

    private final Map<TopType, Map<Integer, List<String>>> tops;

    private final List<String> lines;
    private final String style;
    private final String endpoint;

    private final String dayActive;
    private final String dayInactive;
    private final String weekActive;
    private final String weekInactive;
    private final String monthActive;
    private final String monthInactive;
    private final String allTimeActive;
    private final String allTimeInactive;

    private final String pre;
    private final String selected;
    private final String notSelected;
    private final String separator;

    private final AtomicBoolean canModify = new AtomicBoolean(true);

    public TopHologram(String name, TopFetcher topFetcher) {
        this.name = name;
        this.topFetcher = topFetcher;

        FileConfiguration config = NekoTops.getInstance().getConfig();
        if (config.contains("style." + name)) {
            this.lines = config.getStringList("style." + name + ".lines");
            this.style = config.getString("style." + name + ".style");
        } else {
            this.lines = config.getStringList("style.default-style.lines");
            this.style = config.getString("style.default-style.style");
        }

        this.dayActive = config.getString("style." + name + ".time-style.day.active");
        this.dayInactive = config.getString("style." + name + ".time-style.day.inactive");
        this.weekActive = config.getString("style." + name + ".time-style.week.active");
        this.weekInactive = config.getString("style." + name + ".time-style.week.inactive");
        this.monthActive = config.getString("style." + name + ".time-style.month.active");
        this.monthInactive = config.getString("style." + name + ".time-style.month.inactive");
        this.allTimeActive = config.getString("style." + name + ".time-style.allTime.active");
        this.allTimeInactive = config.getString("style." + name + ".time-style.allTime.inactive");

        this.pre = config.getString("style." + name + ".page-style.pre");
        this.selected = config.getString("style." + name + ".page-style.selected");
        this.notSelected = config.getString("style." + name + ".page-style.not-selected");
        this.separator = config.getString("style." + name + ".page-style.separator");
        this.endpoint = config.getString("endpoint");

        this.tops = new ConcurrentHashMap<>();
    }

    public void update(NekoTops nekoTops) {
        if (!canModify.get()) {
            return;
        }
        canModify.set(false);
        tops.clear();
        AtomicInteger latch = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            for (TopType topType : TopType.values()) {
                int finalI = i;
                Midnight.getInstance().getListeningExecutorService().submit(() -> loadPage(nekoTops, topType, finalI, latch));
            }
        }
        while (true) {
            if (latch.get() == 40) {
                canModify.set(true);
                break;
            } else {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private final Lock concurrencyLock = new ReentrantLock();

    public void loadPage(NekoTops nekoTops, TopType time, int page, AtomicInteger latch) {
        topFetcher.fetch(nekoTops, endpoint, time.getEndpointName(), page, topDatas -> {
            //System.out.println("loading on #" + page + " with "+topDatas.size() + " top datas");
            if (topDatas == null || !topDatas.isEmpty()) {
                loadPage(nekoTops, time, page, topDatas);
            }
            latch.incrementAndGet();
        });
    }

    public void loadPage(NekoTops nekoTops, TopType time, int page, @Nullable List<TopData> topDatas) {
        Map<String, String> prefixes = new ConcurrentHashMap<>();
        LuckPerms luckPerms = LuckPermsProvider.get();
        if (topDatas != null) {
            topDatas.forEach(topData -> {
                User user = luckPerms.getUserManager().loadUser(UUID.fromString(topData.getUuid())).join();
                try {
                    prefixes.put(topData.getName(), user.getCachedData().getMetaData().getPrefix());
                } catch (Exception ignored) {
                }
            });
        }
        List<String> pageLines = new ArrayList<>();
        boolean onlyOne = false;
        for (String line : lines) {
            if (!line.equals("%top%")) {
                pageLines.add(line);
                continue;
            }
            if (topDatas != null) {
                for (TopData topData : topDatas) {
                    pageLines.add(style
                            .replaceAll("%number%", String.valueOf(topData.getPosition()))
                            .replaceAll("%name%", topData.getName())
                            .replaceAll("%uuid%", topData.getUuid())
                            .replaceAll("%prefix%", prefixes.getOrDefault(topData.getName(), ""))
                            .replaceAll("%value%", String.valueOf(topData.getValue()))
                            .replaceAll("%type%", topData.getKind())
                    );
                }
            } else {
                onlyOne = true;
                pageLines.add("&cHubo un error al contactar");
                pageLines.add("&ccon el endpoint, lo sentimos :(");
            }
        }
        pageLines.replaceAll(MessagesUtils::colorize);
        try {
            concurrencyLock.lock();
            Map<Integer, List<String>> topsPosition = tops.computeIfAbsent(time, i -> new ConcurrentHashMap<>());
            if (onlyOne && !topsPosition.isEmpty()) {
                concurrencyLock.unlock();
                return;
            }
            topsPosition.put((onlyOne) ? 0 : page, pageLines);
            concurrencyLock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadHologram(Player player) {
        new PlayerTopHologram(this, player);
    }
}