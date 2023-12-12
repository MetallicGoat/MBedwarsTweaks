package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import java.util.HashMap;

import java.util.Map;
import org.bukkit.entity.Player;

public class BuyGroupTracker {

  private final Map<Player, Map<String, Integer>> trackBuyGroupMap = new HashMap<>();

  public BuyGroupTracker(Arena arena){
    if(arena == null)
      return;

    // Load Players
    for(Player player : arena.getPlayers())
      loadDefaultPlayerBuyGroups(player);
  }

  private void loadDefaultPlayerBuyGroups(Player player) {
    final HashMap<String, Integer> map = new HashMap<>();

    for (BuyGroup group : GameAPI.get().getBuyGroups())
      map.put(group.getName(), 0);

    trackBuyGroupMap.put(player, map);
  }

  public void upgradeLevel(BuyGroup group, int newLevel, Player player){
    if (!trackBuyGroupMap.containsKey(player))
      return;

    // Increment if necessary
    trackBuyGroupMap.get(player).put(group.getName(), newLevel);
  }

  public int getBuyGroupLevel(Player player, String buyGroup){
    if (!trackBuyGroupMap.containsKey(player))
      return 0;

    return trackBuyGroupMap.get(player).getOrDefault(buyGroup, 0);
  }

  public void setBuyGroupLevel(Player player, String buyGroup, int level){
    if (!trackBuyGroupMap.containsKey(player))
      return;

    trackBuyGroupMap.get(player).put(buyGroup, level);
  }
}
