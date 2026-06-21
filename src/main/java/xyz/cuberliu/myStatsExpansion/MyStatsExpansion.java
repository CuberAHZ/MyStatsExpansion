package xyz.cuberliu.myStatsExpansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class MyStatsExpansion extends PlaceholderExpansion {

  private StatsManager statsManager;

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public @NotNull String getAuthor() {
    return "CuberLiu";
  }

  @Override
  public @NotNull String getIdentifier() {
    return "mystats";
  }

  @Override
  public @Nullable String getPlugin() {
    return null;
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  @Override
  public boolean register() {
    this.statsManager = new StatsManager();
    this.statsManager.reloadAllStats();
    return super.register();
  }

  @Override
  public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
    if (params == null || params.isEmpty()) {
      return null;
    }

    String[] parts = params.split("_");
    if (parts.length != 3) {
      return null;
    }

    String listName = parts[0];
    int rank;
    try {
      rank = Integer.parseInt(parts[1]);
    } catch (NumberFormatException e) {
      return null;
    }
    String type = parts[2].toLowerCase();

    Map<String, Double> ranking = statsManager.getRanking(listName);
    if (ranking == null || ranking.isEmpty()) {
      return null;
    }

    var sortedEntries = ranking.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .toList();

    if (rank < 1 || rank > sortedEntries.size()) {
      return null;
    }

    var entry = sortedEntries.get(rank - 1);
    if ("name".equals(type)) {
      return entry.getKey();
    } else if ("value".equals(type)) {
      double value = entry.getValue();
      if (value == (long) value) {
        return String.valueOf((long) value);
      } else {
        return String.format("%.1f", value);
      }
    }

    return null;
  }

  public void reload() {
    if (statsManager != null) {
      statsManager.reloadAllStats();
    }
  }
}