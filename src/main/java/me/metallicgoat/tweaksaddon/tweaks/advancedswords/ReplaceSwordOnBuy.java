package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ReplaceSwordOnBuy implements Listener {
    @EventHandler
    public void onSwordBuy(PlayerBuyInShopEvent event) {

        if (!ConfigValue.replace_sword_on_buy_enabled || !event.getProblems().isEmpty())
            return;

        final Player p = event.getPlayer();
        final PlayerInventory pi = p.getInventory();

        // Checks if player bought a sword
        for (ShopProduct rawProduct : event.getItem().getProducts()) {
            if (rawProduct instanceof ItemShopProduct) {
                final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                for (ItemStack item : is) {
                    if (item.getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(event.getItem().getDisplayName())) {
                        //Clear Wooden Swords
                        if (ConfigValue.replace_sword_on_buy_all_type) {
                            for (ItemStack itemStack : pi) {
                                if (itemStack != null && itemStack.getType().name().contains("SWORD")) {
                                    pi.remove(itemStack);
                                }
                            }
                        } else {
                            pi.remove(ToolSwordHelper.WOOD_SWORD);
                        }
                    }
                    break;
                }
            }
        }
    }
}