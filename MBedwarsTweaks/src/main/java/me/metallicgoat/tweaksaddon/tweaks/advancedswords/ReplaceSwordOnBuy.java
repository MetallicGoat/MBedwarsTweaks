package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ReplaceSwordOnBuy implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onSwordBuy(PlayerBuyInShopEvent event) {
    if (!SwordsToolsConfig.replace_sword_on_buy_enabled || !event.getProblems().isEmpty())
      return;

    final Player player = event.getPlayer();
    final PlayerInventory pi = player.getInventory();

    // Checks if player bought a sword
    for (ShopProduct rawProduct : event.getItem().getProducts()) {
      if (!(rawProduct instanceof ItemShopProduct))
        continue;

      final ItemStack[] givenAll = ((ItemShopProduct) rawProduct).getItemStacks();

      for (ItemStack given : givenAll) {
        if (!ToolSwordHelper.isSword(given.getType()) || !ToolSwordHelper.isNotToIgnore(given))
          continue;

        // Clear Wooden Swords
        // TODO check ignore list for wood swords
        if (!SwordsToolsConfig.replace_sword_on_buy_all_type) {
          pi.remove(ToolSwordHelper.WOOD_SWORD);
          break;
        }

        final ItemStack[] contents = pi.getContents();

        for (int i=0; i<contents.length; i++) {
          final ItemStack is = contents[i];

          if (is == null
              || !ToolSwordHelper.isSword(is.getType())
              || !ToolSwordHelper.isNotToIgnore(is))
            continue;

          if (ToolSwordHelper.getSwordToolLevel(is.getType()) < ToolSwordHelper.getSwordToolLevel(given.getType()))
            pi.setItem(i, null);
        }
      }
    }
  }
}