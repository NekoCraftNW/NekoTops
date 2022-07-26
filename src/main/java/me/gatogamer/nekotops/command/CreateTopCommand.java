package me.gatogamer.nekotops.command;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import me.gatogamer.midnight.commons.io.FileIO;
import me.gatogamer.midnight.spigot.utils.NyaLocation;
import me.gatogamer.nekotops.NekoTops;
import me.gatogamer.nekotops.top.HologramTopData;
import me.gatogamer.nekotops.top.TopFetcher;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class CreateTopCommand implements CommandExecutor {
    private final NekoTops nekoTops;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 3) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUso: /createtop <name> <gamemode> <topKind>"));
        }
        String name = args[0];
        String gamemode = args[1];
        String topKind = args[2];

        HologramTopData hologramTopData = new HologramTopData(name, gamemode, topKind);
        NyaLocation nyaLocation = new NyaLocation(player.getLocation());
        TopFetcher topFetcher = new TopFetcher(name, hologramTopData, nyaLocation);

        File file = new File(nekoTops.getDataFolder(), "tops/"+name+".json");
        FileIO.writeFile(file, new GsonBuilder().setPrettyPrinting().create().toJson(topFetcher));

        nekoTops.getTopManager().getTops().put(topFetcher.getName(), topFetcher);
        return true;
    }
}