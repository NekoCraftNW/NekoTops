package me.gatogamer.nekotops.listeners;

import lombok.RequiredArgsConstructor;
import me.gatogamer.nekotops.NekoTops;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class ConnectionListener implements Listener {
    private final NekoTops nekoTops;

    @EventHandler
    public void onConnection(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        nekoTops.getHologramManager().getTops().forEach((s, topHologram) -> topHologram.loadHologram(player));
    }
}