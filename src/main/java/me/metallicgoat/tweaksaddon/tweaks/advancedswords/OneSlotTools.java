package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerOpenShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopPage;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class OneSlotTools implements Listener {

    @EventHandler
    public void onShopOpen(PlayerOpenShopEvent event) {
        final ShopPage page = event.getPage();
        if (!ConfigValue.one_slot_tools_enabled || page == null)
            return;

        final Player player = event.getPlayer();
        final Iterator<? extends ShopItem> it = page.getItems().iterator();
        final List<String> reAddGroups = new ArrayList<>();
        final List<ShopItem> removeItems = new ArrayList<>();

        while (it.hasNext()) {
            final ShopItem item = it.next();
            final BuyGroup group = item.getBuyGroup();
            if (group == null)
                continue;

            if (ToolSwordHelper.oneSlotItemGroups.containsKey(group.getName())) {
                removeItems.add(item);

                if (!reAddGroups.contains(group.getName()))
                    reAddGroups.add(group.getName());
            }
        }

        for (ShopItem item : removeItems)
            page.removeShopItem(item);

        // TODO force set force slot to config values (maybe when loading do this)
        for (String string : reAddGroups)
            page.addShopItem(ToolSwordHelper.getNextTierButton(string, player));
    }
}
