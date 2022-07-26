package me.gatogamer.nekotops;

import lombok.Getter;
import me.gatogamer.midnight.spigot.MidnightModule;
import me.gatogamer.nekotops.command.CreateTopCommand;
import me.gatogamer.nekotops.top.TopManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class NekoTops extends JavaPlugin {
    private final MidnightImpl midnightImpl = new MidnightImpl();
    private TopManager topManager;

    @Override
    public void onLoad() {
        if (Bukkit.getPluginManager().getPlugin("Midnight") != null) {
            log("&bMidnight &7has been detected, &chooking with it!");
            MidnightModule.addInjectable(midnightImpl);
            log("&bMidnight &7hook has been started successfully, waiting until full boot!");
        } else {
            log("&4Cannot find &bMidnight&4! &cIs it on the plugins folder?");
            log("&cShutting down. Reason: &bMidnight not found&c.");
            System.exit(0);
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        topManager = new TopManager(this);

        getCommand("createtop").setExecutor(new CreateTopCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void log(String s) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bNekoTops &8> &7" + s));
    }
}