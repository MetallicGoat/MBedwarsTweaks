package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameDeathEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.ShopItem;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.HashMap;

public class DowngradeTools implements Listener {

    public static final HashMap<Player, Integer> pickaxeHashMap = new HashMap<>();
    public static final HashMap<Player, Integer> axeHashMap = new HashMap<>();

    @EventHandler
    public void onStart(RoundStartEvent event) {
        for (Player player : event.getArena().getPlayers()) {
            pickaxeHashMap.put(player, 0);
            axeHashMap.put(player, 0);
        }
    }

    @EventHandler
    public void onRespawn(PlayerIngameRespawnEvent event) {

        if (!ConfigValue.degrading_tool_groups || !ConfigValue.advanced_tool_replacement_enabled)
            return;

        final Player player = event.getPlayer();
        final Arena arena = event.getArena();
        final Collection<BuyGroup> buyGroups = GameAPI.get().getBuyGroups();

        for (BuyGroup buyGroup : buyGroups) {

            final String buyGroupName = buyGroup.getName();

            if (buyGroupName.equalsIgnoreCase("pickaxe")
                    || buyGroup.getName().equalsIgnoreCase("axe")) {

                final int level = buyGroupName.equalsIgnoreCase("pickaxe") ? pickaxeHashMap.get(player) : axeHashMap.get(player);
                final Collection<? extends ShopItem> shopItems = buyGroup.getItems(level);

                if (shopItems == null)
                    return;

                for (ShopItem item : shopItems) {
                    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> item.getProducts().forEach(shopProduct -> {
                        arena.setBuyGroupLevel(player, buyGroup, level);
                        shopProduct.give(event.getPlayer(), event.getArena().getPlayerTeam(player), event.getArena(), 1);
                    }), 1L);
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void levelOneBuy(PlayerBuyInShopEvent event) {
        final Player player = event.getPlayer();

        //If enabled, and item has buy-group
        if (event.getProblems().isEmpty() && event.getItem().hasBuyGroup()) {

            final BuyGroup group = event.getItem().getBuyGroup();
            final int level = event.getItem().getBuyGroupLevel();

            if (group == null)
                return;

            //if proper buy-group
            if (group.getName().equalsIgnoreCase("pickaxe"))
                pickaxeHashMap.replace(player, level);
            else if (group.getName().equalsIgnoreCase("axe"))
                axeHashMap.replace(player, level);
        }
    }

    @EventHandler
    public void onRespawn(PlayerIngameDeathEvent event) {

        if (!ConfigValue.degrading_tool_groups || !ConfigValue.advanced_tool_replacement_enabled)
            return;

        final Player player = event.getPlayer();
        final Arena arena = event.getArena();
        final Collection<BuyGroup> buyGroups = GameAPI.get().getBuyGroups();

        for (BuyGroup buyGroup : buyGroups) {

            if (buyGroup.getName().contains("axe")) {

                final int level = arena.getBuyGroupLevel(player, buyGroup);

                if (level > 1) {

                    if (buyGroup.getName().equalsIgnoreCase("pickaxe")) {
                        pickaxeHashMap.put(player, level - 1);
                        arena.setBuyGroupLevel(player, buyGroup, level - 1);
                    } else if (buyGroup.getName().equalsIgnoreCase("axe")) {
                        axeHashMap.put(player, level - 1);
                        arena.setBuyGroupLevel(player, buyGroup, level - 1);
                    }
                }
            }
        }
    }
}
