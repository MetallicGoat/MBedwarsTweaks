package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import java.util.HashMap;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BuyGroupTracker {

  private final HashMap<OfflinePlayer, HashMap<String, Integer>> trackBuyGroupMap = new HashMap<>();

  public BuyGroupTracker(Arena arena){
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
    final HashMap<String, Integer> map = trackBuyGroupMap.get(player);

    if (map == null) {
      loadDefaultPlayerBuyGroups(player);
      return;
    }

    // Increment if necessary
    map.put(group.getName(), newLevel);
  }

  public int getBuyGroupLevel(Player player, String buyGroup){
    final HashMap<String, Integer> playerMap = trackBuyGroupMap.get(player);

    if(playerMap == null){
      loadDefaultPlayerBuyGroups(player);
      return 0;
    }

    return playerMap.getOrDefault(buyGroup, 0);
  }

  public void setBuyGroupLevel(Player player, String buyGroup, int level){
    final HashMap<String, Integer> playerMap = trackBuyGroupMap.get(player);

    if(playerMap == null)
      return;

    playerMap.put(buyGroup, level);
  }
}
