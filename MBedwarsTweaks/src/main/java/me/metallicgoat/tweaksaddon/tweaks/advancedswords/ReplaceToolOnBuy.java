package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ReplaceToolOnBuy implements Listener {

  @EventHandler
  public void onToolBuy(PlayerBuyInShopEvent event) {
    if (!SwordsToolsConfig.advanced_tool_replacement_enabled)
      return;

    final BuyGroup group = event.getItem().getBuyGroup();

    // If enabled, and item has buy-group
    if (group != null
        && event.getProblems().isEmpty()
        && SwordsToolsConfig.advanced_tool_replacement_buygroups.contains(group.getName())) {

      clearOld(ToolSwordHelper.getToolInShopProduct(event.getItem()), event.getPlayer());
    }
  }

  private void clearOld(Material tool, Player p) {
    final boolean isClearingPickaxe = ToolSwordHelper.isPickaxe(tool);
    final PlayerInventory inv = p.getInventory();

    for (int i = 0; i < p.getInventory().getSize(); i++) {
      final ItemStack itemStack = inv.getItem(i);

      // HACK - Auto replace shears
      if (tool == Material.SHEARS && itemStack != null && itemStack.getType() == Material.SHEARS) {
        inv.setItem(i, null);
        continue;
      }

      if (itemStack == null ||
          !ToolSwordHelper.isTool(itemStack.getType()) ||
          !ToolSwordHelper.isNotToIgnore(itemStack))
        continue;

      final boolean isCheckingPickaxe = ToolSwordHelper.isPickaxe(itemStack.getType());
      final boolean match = (isCheckingPickaxe && isClearingPickaxe) || (!isCheckingPickaxe && !isClearingPickaxe);

      if (match && ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType()))
        inv.setItem(i, null);
    }
  }
}