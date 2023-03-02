package me.gatogamer.nekotops.hologram;

import me.gatogamer.midnight.spigot.hologram.Hologram;
import me.gatogamer.midnight.spigot.hologram.HologramClick;
import me.gatogamer.midnight.spigot.utils.MessagesUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
public class PlayerTopHologram {
    private final TopHologram topHologram;
    private final Player player;
    private final Hologram hologram;
    private TopType topType;
    private int page;
    private long lastClick = 0L;

    public PlayerTopHologram(TopHologram topHologram, Player player) {
        this.topHologram = topHologram;
        this.player = player;
        this.hologram = new Hologram(topHologram.getName(), player, topHologram.getTopFetcher().getNyaLocation().toLocation(),
                new HologramClick(
                        () -> {
                            if (isCooldown()) {
                                return;
                            }
                            if (player.isSneaking()) {
                                topType = topType.previous();
                                page = 0;
                                doRender();
                                player.playSound(player.getLocation(), Sound.DIG_GRASS, 20F, 1.0F);
                                return;
                            }
                            page--;
                            if (page == -1) {
                                try {
                                    page = topHologram.getTops().get(topType).size() - 1;
                                } catch (Exception e) {
                                    page = 0;
                                }
                            }
                            doRender();
                            player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
                        },
                        () -> {
                            if (isCooldown()) {
                                return;
                            }
                            if (player.isSneaking()) {
                                topType = topType.next();
                                page = 0;
                                doRender();
                                player.playSound(player.getLocation(), Sound.DIG_SAND, 20F, 1.0F);
                                return;
                            }
                            page++;
                            try {
                                int pages = topHologram.getTops().get(topType).size() - 1;
                                if (page > pages) {
                                    page = 0;
                                }
                            } catch (Exception e) {
                                page = 0;
                            }
                            doRender();
                            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                        }
                )
        );
        this.topType = TopType.ALL_TIME;
        this.page = 0;
        doRender();
    }

    public void doRender() {
        Map<Integer, List<String>> pages = topHologram.getTops().get(topType);
        List<String> lines;
        String pagesText;
        try {
            lines = new ArrayList<>(pages.getOrDefault(page, pages.get(0)));
            pagesText = getPagesText(pages.size(), page);
        } catch (Exception e) {
            lines = new ArrayList<>(topHologram.getLines());
            pagesText = getPagesText(0, page);
        }
        String finalPagesText = pagesText;
        lines.replaceAll(s ->
                MessagesUtils.colorize(s
                        .replace("%pages%", finalPagesText)
                        .replace("%top%", "&cEste top aún no tiene jugadores.")
                        .replace("%day%", topType == TopType.DAY ? topHologram.getDayActive() : topHologram.getDayInactive())
                        .replace("%week%", topType == TopType.WEEK ? topHologram.getWeekActive() : topHologram.getWeekInactive())
                        .replace("%month%", topType == TopType.MONTH ? topHologram.getMonthActive() : topHologram.getMonthInactive())
                        .replace("%allTime%", topType == TopType.ALL_TIME ? topHologram.getAllTimeActive() : topHologram.getAllTimeInactive())
                )
        );
        hologram.setLines(lines.toArray(new String[0]));
    }

    public String getPagesText(int pages, int page) {
        List<String> nums = new ArrayList<>();
        if (pages > 1) {
            if (page - 1 == -1) {
                nums.add(topHologram.getNotSelected() + pages);
            } else {
                nums.add(topHologram.getNotSelected() + (page));
            }
            nums.add((topHologram.getSelected()) + (page + 1));
            if (pages == page + 1) {
                nums.add(topHologram.getNotSelected() + 1);
            } else {
                nums.add((topHologram.getNotSelected()) + (page + 2));
            }
        } else {
            nums.add((topHologram.getSelected()) + (page + 1));
        }
        return topHologram.getPre() + String.join(topHologram.getSeparator(), nums);
    }

    public boolean isCooldown() {
        if (System.currentTimeMillis() - lastClick > 50L) {
            lastClick = System.currentTimeMillis();
            if (!topHologram.getCanModify().get()) {
                player.sendMessage(MessagesUtils.colorize("&c¡Espera! Este top se está actualizando."));
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
}