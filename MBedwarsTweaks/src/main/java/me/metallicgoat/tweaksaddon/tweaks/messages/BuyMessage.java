package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class BuyMessage implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onBuyEvent(PlayerBuyInShopEvent event) {
    if (!MainConfig.buy_message_enabled || !event.getProblems().isEmpty())
      return;

    final Player p = event.getPlayer();
    final String product = event.getItem().getDisplayName();
    final int amount = getAmount(event.getItem());
    final String strAmount = Integer.toString(getAmount(event.getItem()));
    final String baseMessage = amount == 0 ? MainConfig.buy_message_amountless : MainConfig.buy_message; // e.g. if command is used as product, amount would be 0

    Message.build(baseMessage)
        .placeholder("product", product)
        .placeholder("amount", strAmount)
        .send(p);
  }

  private int getAmount(ShopItem item) {
    int count = 0;
    for (ShopProduct rawProduct : item.getProducts()) {
      if (rawProduct instanceof ItemShopProduct) {
        final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
        for (ItemStack itemStack : is) {
          count += itemStack.getAmount();
        }
      }
    }
    return count;
  }
}