package me.gatogamer.nekotops;

import lombok.Getter;
import me.gatogamer.midnight.spigot.hologram.HologramLine;
import me.gatogamer.midnight.spigot.hologram.HologramPacketAdapter;
import me.gatogamer.nekotops.command.CreateTopCommand;
import me.gatogamer.nekotops.hologram.HologramManager;
import me.gatogamer.nekotops.listeners.ConnectionListener;
import me.gatogamer.nekotops.tasks.TopUpdaterTask;
import me.gatogamer.nekotops.top.TopManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class NekoTops extends JavaPlugin {
    @Getter
    private static NekoTops instance;

    private TopManager topManager;
    private HologramManager hologramManager;
    private TopUpdaterTask topUpdaterTask;

    @Override
    public void onLoad() {
        if (Bukkit.getPluginManager().getPlugin("Midnight") != null) {
            log("&bMidnight &7has been detected, &chooking with it!");
        } else {
            log("&4Cannot find &bMidnight&4! &cIs it on the plugins folder?");
            log("&cShutting down. Reason: &bMidnight not found&c.");
            System.exit(0);
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        topManager = new TopManager(this);
        hologramManager = new HologramManager(topManager);

        topUpdaterTask = new TopUpdaterTask(this);
        topUpdaterTask.run();
        long minutes = 1L;
        long time = minutes * 60 * 20L;
        topUpdaterTask.runTaskTimerAsynchronously(this, time, time);

        Bukkit.getOnlinePlayers().forEach(player ->
                hologramManager.getTops().forEach((s, topHologram) -> topHologram.loadHologram(player))
        );

        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);

        getCommand("createtop").setExecutor(new CreateTopCommand(this));
    }

    @Override
    public void onDisable() {
        HologramPacketAdapter.getHolograms().forEach((player, holograms) -> {
            holograms.removeIf(hologram -> {
                if (topManager.getTops().get(hologram.getName()) != null) {
                    hologram.getLines().forEach(HologramLine::hide);
                    return true;
                } else {
                    return false;
                }
            });
        });
    }

    public void log(String s) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bNekoTops &8> &7" + s));
    }
}