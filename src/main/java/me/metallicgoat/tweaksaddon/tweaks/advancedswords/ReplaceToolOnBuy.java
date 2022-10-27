package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ReplaceToolOnBuy implements Listener {

    @EventHandler
    public void onToolBuy(PlayerBuyInShopEvent event) {
        if (!ConfigValue.advanced_tool_replacement_enabled)
            return;

        final BuyGroup group = event.getItem().getBuyGroup();

        //If enabled, and item has buy-group
        if (group != null
                && event.getProblems().isEmpty()
                && ConfigValue.advanced_tool_replacement_buygroups.contains(group.getName())) {

            final Player player = event.getPlayer();
            final int currentLevel = ToolSwordHelper.getBuyGroupLevel(group.getName(), player);

            // If getting higher tier
            if (ConfigValue.advanced_tool_replacement_force_ordered) {
                // current + 1 = buying level
                if (event.getItem().getBuyGroupLevel() != currentLevel + 1)
                    ToolSwordHelper.addShopProblem(event, ConfigValue.advanced_tool_replacement_force_ordered_problem);

            }

            clearOld(ToolSwordHelper.getToolInShopProduct(event.getItem()), player);
        }
    }

    private void clearOld(Material tool, Player p) {
        final boolean isClearingPickaxe = ToolSwordHelper.isPickaxe(tool);

        for (ItemStack itemStack : p.getInventory()) {
            if (itemStack == null ||
                    !ToolSwordHelper.isTool(itemStack.getType()) ||
                    !ToolSwordHelper.isNotToIgnore(itemStack))
                continue;

            final boolean isCheckingPickaxe = ToolSwordHelper.isPickaxe(itemStack.getType());
            final boolean match = (isCheckingPickaxe && isClearingPickaxe) || (!isCheckingPickaxe && !isClearingPickaxe);

            if (match && ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType()))
                p.getInventory().remove(itemStack);

        }
    }
}