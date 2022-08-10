package me.gatogamer.nekotops.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import lombok.Getter;
import me.gatogamer.nekotops.NekoTops;
import me.gatogamer.nekotops.top.TopFetcher;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final Hologram hologram;
    private final List<String> lines;
    private final String style;
    private final String endpoint;

    public TopHologram(String name, TopFetcher topFetcher) {
        this.name = name;
        this.topFetcher = topFetcher;

        this.hologram = HologramsAPI.createHologram(NekoTops.getInstance(), topFetcher.getNyaLocation().toLocation());
        FileConfiguration config = NekoTops.getInstance().getConfig();
        if (config.contains("style." + name)) {
            this.lines = config.getStringList("style." + name + ".lines");
            this.style = config.getString("style." + name + ".style");
        } else {
            this.lines = config.getStringList("style.default-style.lines");
            this.style = config.getString("style.default-style.style");
        }
        this.endpoint = config.getString("endpoint");
    }

    public void update(NekoTops nekoTops) {
        topFetcher.fetch(nekoTops, endpoint, topDatas -> {
            int hologramLines = hologram.size();
            AtomicInteger usedLines = new AtomicInteger();
            lines.forEach(line -> {
                if (line.equals("%top%")) {
                    topDatas.forEach(topData ->
                            reuseLineIfCan(usedLines.get(), style
                                    .replaceAll("%number%", String.valueOf(topData.getPosition()))
                                    .replaceAll("%name%", topData.getName())
                                    .replaceAll("%value%", String.valueOf(topData.getValue()))
                                    .replaceAll("%type%", topData.getKind())
                            )
                    );
                } else {
                    reuseLineIfCan(usedLines.get(), line);
                }
                usedLines.incrementAndGet();
            });
            if (hologramLines > usedLines.get()) {
                int linesToRemove = hologramLines - usedLines.get();
                for (int i = 0; i < linesToRemove; i++) {
                    hologram.removeLine(hologram.size() - 1);
                }
            }
        });
    }

    public void reuseLineIfCan(int line, String text) {
        HologramLine hologramLine = hologram.getLine(line);
        if (hologramLine != null) {
            if (hologramLine instanceof TextLine) {
                TextLine textLine = (TextLine) hologramLine;
                textLine.setText(text);
            }
        } else {
            hologram.appendTextLine(text);
        }
    }
}