package me.gatogamer.nekotops.tasks;

import lombok.RequiredArgsConstructor;
import me.gatogamer.nekotops.NekoTops;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class TopUpdaterTask extends BukkitRunnable {
    private final NekoTops nekoTops;

    @Override
    public void run() {
        nekoTops.getHologramManager().getTops().forEach((s, topHologram) ->
                topHologram.update(nekoTops)
        );
    }
}