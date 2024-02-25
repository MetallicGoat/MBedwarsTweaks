package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.game.shop.BuyGroup;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class BuyGroupTracker {

  private final Map<UUID, Map<String, Integer>> trackBuyGroupMap = new HashMap<>();

  public void upgradeLevel(BuyGroup group, int newLevel, Player player) {
    setBuyGroupLevel(player, group.getName(), newLevel);
  }

  public int getBuyGroupLevel(Player player, String buyGroup) {
    final Map<String, Integer> map = trackBuyGroupMap.get(player.getUniqueId());

    if (map == null)
      return 0;

    return map.getOrDefault(buyGroup, 0);
  }

  public void setBuyGroupLevel(Player player, String buyGroup, int level) {
    final Map<String, Integer> map = this.trackBuyGroupMap.computeIfAbsent(
        player.getUniqueId(),
        g0 -> new HashMap<>());

    map.put(buyGroup, level);
  }
}
