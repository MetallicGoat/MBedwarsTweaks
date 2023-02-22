package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.ShopItem;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;

public class DegradingBuyGroups implements Listener {

    @EventHandler
    public void onRespawn(PlayerIngameRespawnEvent event) {
        if (!ConfigValue.degrading_buygroups_enabled)
            return;

        final Player player = event.getPlayer();
        final Arena arena = event.getArena();

        for (BuyGroup buyGroup : GameAPI.get().getBuyGroups()) {
            final String buyGroupName = buyGroup.getName();

            // TODO case sensitive
            if(!ConfigValue.degrading_buygroups.contains(buyGroupName))
                continue;

            int level = ToolSwordHelper.trackBuyGroupMap.get(player).getOrDefault(buyGroupName, 0);

            // TODO config to set min level here?
            if(level > 1) {
                level -= 1;
                ToolSwordHelper.trackBuyGroupMap.get(player).put(buyGroupName, level);
                arena.setBuyGroupLevel(player, buyGroup, level);
            }

            // Give item
            final Collection<? extends ShopItem> shopItems = buyGroup.getItems(level);
            if (shopItems == null)
                return;

            final Team team = arena.getPlayerTeam(player);
            for (ShopItem item : shopItems)
                ToolSwordHelper.givePlayerShopItem(arena, team, player, item);
        }
    }
}