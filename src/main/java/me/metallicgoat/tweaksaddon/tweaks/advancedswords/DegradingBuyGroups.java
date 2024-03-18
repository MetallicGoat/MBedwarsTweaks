package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.ShopItem;
import java.util.Collection;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DegradingBuyGroups implements Listener {

  @EventHandler
  public void onRespawn(PlayerIngameRespawnEvent event) {
    if (!SwordsToolsConfig.degrading_buygroups_enabled)
      return;

    final Player player = event.getPlayer();
    final Arena arena = event.getArena();

    for (String name : SwordsToolsConfig.degrading_buygroups) {
      final BuyGroup buyGroup = GameAPI.get().getBuyGroup(name);

      if (buyGroup == null)
        continue;

      final BuyGroupTracker tracker = ToolSwordHelper.getBuyGroupTracker(arena);
      int newLevel = tracker.getBuyGroupLevel(player, name);

      if (buyGroup.getLevels().contains(newLevel - 1))
        newLevel--;

      tracker.setBuyGroupLevel(player, name, newLevel);
      arena.setBuyGroupLevel(player, buyGroup, newLevel);

      // Give items
      final Collection<? extends ShopItem> shopItems = buyGroup.getItems(newLevel);

      if (shopItems == null)
        continue;

      final Team team = arena.getPlayerTeam(player);

      for (ShopItem item : shopItems)
        ToolSwordHelper.givePlayerShopItem(arena, team, player, item);
    }
  }
}