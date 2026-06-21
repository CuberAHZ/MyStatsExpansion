package xyz.cuberliu.myStatsExpansion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatsManager {

  private final Map<String, Map<String, Double>> cache = new ConcurrentHashMap<>();
  private final File statsFolder;

  private static final String LIST_MINE = "挖掘榜";
  private static final String LIST_PLACE = "放置榜";
  private static final String LIST_ONLINE = "在线时间榜";
  private static final String LIST_DEATHS = "死亡榜";
  private static final String LIST_MOB_KILLS = "击杀生物榜";
  private static final String LIST_PLAYER_KILLS = "击杀玩家榜";

  public StatsManager() {
    this.statsFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
  }

  public void reloadAllStats() {
    cache.clear();
    if (!statsFolder.exists() || !statsFolder.isDirectory()) {
      return;
    }

    File[] statFiles = statsFolder.listFiles((dir, name) -> name.endsWith(".json"));
    if (statFiles == null || statFiles.length == 0) {
      return;
    }

    Map<String, Map<String, Double>> allStats = new HashMap<>();
    allStats.put(LIST_MINE, new HashMap<>());
    allStats.put(LIST_PLACE, new HashMap<>());
    allStats.put(LIST_ONLINE, new HashMap<>());
    allStats.put(LIST_DEATHS, new HashMap<>());
    allStats.put(LIST_MOB_KILLS, new HashMap<>());
    allStats.put(LIST_PLAYER_KILLS, new HashMap<>());

    for (File file : statFiles) {
      String uuid = file.getName().replace(".json", "");
      String playerName = getPlayerName(uuid);
      if (playerName == null) {
        continue;
      }

      try (FileReader reader = new FileReader(file)) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        if (!root.has("stats")) continue;
        JsonObject stats = root.getAsJsonObject("stats");

        // 挖掘 (minecraft:mined)
        double mineTotal = 0;
        if (stats.has("minecraft:mined")) {
          JsonObject mined = stats.getAsJsonObject("minecraft:mined");
          for (Map.Entry<String, JsonElement> entry : mined.entrySet()) {
            mineTotal += entry.getValue().getAsDouble();
          }
        }
        allStats.get(LIST_MINE).merge(playerName, mineTotal, Double::sum);

        // 放置 (minecraft:used)
        double placeTotal = 0;
        if (stats.has("minecraft:used")) {
          JsonObject used = stats.getAsJsonObject("minecraft:used");
          for (Map.Entry<String, JsonElement> entry : used.entrySet()) {
            placeTotal += entry.getValue().getAsDouble();
          }
        }
        allStats.get(LIST_PLACE).merge(playerName, placeTotal, Double::sum);

        // 在线时间、死亡、击杀 (minecraft:custom)
        if (stats.has("minecraft:custom")) {
          JsonObject custom = stats.getAsJsonObject("minecraft:custom");
          double online = custom.has("minecraft:play_time") ?
                  custom.get("minecraft:play_time").getAsDouble() : 0;
          allStats.get(LIST_ONLINE).merge(playerName, online, Double::sum);
          double deaths = custom.has("minecraft:deaths") ?
                  custom.get("minecraft:deaths").getAsDouble() : 0;
          allStats.get(LIST_DEATHS).merge(playerName, deaths, Double::sum);
          double mobKills = custom.has("minecraft:mob_kills") ?
                  custom.get("minecraft:mob_kills").getAsDouble() : 0;
          allStats.get(LIST_MOB_KILLS).merge(playerName, mobKills, Double::sum);
          double playerKills = custom.has("minecraft:player_kills") ?
                  custom.get("minecraft:player_kills").getAsDouble() : 0;
          allStats.get(LIST_PLAYER_KILLS).merge(playerName, playerKills, Double::sum);
        }
      } catch (Exception e) {
        // 静默处理，不输出任何日志
      }
    }

    cache.putAll(allStats);
  }

  public Map<String, Double> getRanking(String listName) {
    return cache.getOrDefault(listName, Collections.emptyMap());
  }

  public void clearCache() {
    cache.clear();
  }

  private String getPlayerName(String uuid) {
    try {
      Player player = Bukkit.getPlayer(UUID.fromString(uuid));
      if (player != null) return player.getName();
      return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
