package me.gatogamer.nekotops.top;

import lombok.Data;
import me.gatogamer.midnight.commons.concurrent.callback.Callback;
import me.gatogamer.midnight.commons.concurrent.http.JsonObjectHttpConnection;
import me.gatogamer.midnight.libs.gson.JsonArray;
import me.gatogamer.midnight.libs.gson.JsonElement;
import me.gatogamer.midnight.libs.gson.JsonObject;
import me.gatogamer.midnight.spigot.Midnight;
import me.gatogamer.midnight.spigot.utils.NyaLocation;
import me.gatogamer.nekotops.NekoTops;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@Data
public class TopFetcher {
    private final String name;
    private final HologramTopData hologramTopData;
    private final NyaLocation nyaLocation;

    public void fetch(String endpoint, String time, Callback<List<TopData>> callback) {
        fetch(NekoTops.getPlugin(NekoTops.class), endpoint, time, 0, callback);
    }

    public void fetch(NekoTops nekoTops, String endpoint, String time, Callback<List<TopData>> callback) {
        fetch(nekoTops, endpoint, time, 0, callback);
    }

    public void fetch(NekoTops nekoTops, String endpoint, String time, int page, Callback<List<TopData>> callback) {
        String fixedEndpoint = endpoint
                .replace("%gamemode%", hologramTopData.getGamemode())
                .replace("%kind%", hologramTopData.getTopKind())
                .replace("%time%", time)
                .replace("%page%", String.valueOf(page));
        JsonObjectHttpConnection jsonObjectHttpConnection = new JsonObjectHttpConnection(
                fixedEndpoint
        );
        JsonElement jsonElement = jsonObjectHttpConnection.ask().getResponse().orElse(null);
        AtomicInteger position = new AtomicInteger();
        if (jsonElement != null) {
            JsonObject baseObject = jsonElement.getAsJsonObject();
            JsonObject dataObject = baseObject.get("data").getAsJsonObject();
            JsonArray topsObject = dataObject.get("tops").getAsJsonArray();
            List<TopData> topDatas = new ArrayList<>();
            topsObject.forEach(topObject -> {
                TopData topData = Midnight.getInstance().getGson().fromJson(topObject, TopData.class);
                topDatas.add(topData);
                topData.setPosition((page * 10) + position.incrementAndGet());
            });
            callback.call(topDatas);
        } else {
            nekoTops.log("&7The endpoint &c" + fixedEndpoint + " &7didn't returned the expected data &8> &b" + jsonObjectHttpConnection.getResponse());
            callback.call(null);
        }
    }
}