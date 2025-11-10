package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
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

      clearOld(group, event.getItem().getBuyGroupLevel(), event.getPlayer());
    }
  }

  private void clearOld(BuyGroup purchasedGroup, int level, Player player) {
    final PlayerInventory inv = player.getInventory();

    for (int i = 0; i < player.getInventory().getSize(); i++) {
      final ItemStack is = inv.getItem(i);

      if (is == null)
        continue;

      final ItemShopProduct invProduct = GameAPI.get().getItemShopProduct(is);

      if (invProduct == null || invProduct.getItem().getBuyGroupLevel() < level)
        continue;

      final BuyGroup checkingGroup = invProduct.getItem().getBuyGroup();

      // Check names, not instance (cloneable). Names are unique
      if (checkingGroup == null || !checkingGroup.getName().equals(purchasedGroup.getName()))
        continue;

      // Remove items
      inv.setItem(i, null);
    }
  }
}