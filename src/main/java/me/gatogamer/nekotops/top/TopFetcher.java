package me.gatogamer.nekotops.top;

import lombok.Data;
import me.gatogamer.midnight.commons.concurrent.callback.Callback;
import me.gatogamer.midnight.commons.concurrent.http.async.AsyncJsonObjectHttpConnection;
import me.gatogamer.midnight.libs.gson.JsonArray;
import me.gatogamer.midnight.libs.gson.JsonObject;
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

    public void fetch(String endpoint, Callback<List<TopData>> callback) {
        fetch(NekoTops.getPlugin(NekoTops.class), endpoint, 0, callback);
    }

    public void fetch(NekoTops nekoTops, String endpoint, Callback<List<TopData>> callback) {
        fetch(nekoTops, endpoint, 0, callback);
    }

    public void fetch(NekoTops nekoTops, String endpoint, int page, Callback<List<TopData>> callback) {
        new AsyncJsonObjectHttpConnection(
                endpoint
                        .replace("%gamemode%", hologramTopData.getGamemode())
                        .replace("%kind%", hologramTopData.getTopKind())
                        .replace("%page%", String.valueOf(page))
        ).ask(nekoTops.getMidnightImpl().getListeningExecutorService()).addCallback(jsonElementResponse -> {
            AtomicInteger position = new AtomicInteger();
            jsonElementResponse.getResponse().ifPresent(jsonElement -> {
                JsonObject baseObject = jsonElement.getAsJsonObject();
                JsonObject dataObject = baseObject.get("data").getAsJsonObject();
                JsonArray topsObject = dataObject.get("tops").getAsJsonArray();
                List<TopData> topDatas = new ArrayList<>();
                topsObject.forEach(topObject -> {
                    TopData topData = nekoTops.getMidnightImpl().getGson().fromJson(topObject, TopData.class);
                    topDatas.add(topData);
                    topData.setPosition((page + 1) * position.incrementAndGet());
                });
                callback.call(topDatas);
            });
        });
    }
}