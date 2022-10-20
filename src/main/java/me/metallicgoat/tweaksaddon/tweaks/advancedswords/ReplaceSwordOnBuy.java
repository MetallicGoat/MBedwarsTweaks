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

import java.util.ArrayList;
import java.util.List;

public class ReplaceSwordOnBuy implements Listener {
    // TODO cleanup
    @EventHandler
    public void onSwordBuy(PlayerBuyInShopEvent event) {
        if (!ConfigValue.replace_sword_on_buy_enabled || !event.getProblems().isEmpty())
            return;

        final Player p = event.getPlayer();
        final PlayerInventory pi = p.getInventory();
        final List<ItemStack> removeItems = new ArrayList<>();

        // Checks if player bought a sword
        for (ShopProduct rawProduct : event.getItem().getProducts()) {
            if (!(rawProduct instanceof ItemShopProduct))
                continue;

            final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
            for (ItemStack item : is) {
                if (!ToolSwordHelper.isSword(item.getType()) || !ToolSwordHelper.isNotToIgnore(item))
                    continue;

                //Clear Wooden Swords
                if (!ConfigValue.replace_sword_on_buy_all_type) {
                    pi.remove(ToolSwordHelper.WOOD_SWORD);
                    break;
                }

                for (ItemStack itemStack : pi) {
                    if (itemStack == null
                            || !ToolSwordHelper.isSword(itemStack.getType())
                            || !ToolSwordHelper.isNotToIgnore(itemStack))
                        continue;

                    if (ToolSwordHelper.getSwordToolLevel(itemStack.getType()) < ToolSwordHelper.getSwordToolLevel(item.getType()))
                        removeItems.add(itemStack);
                    else
                        ToolSwordHelper.addShopProblem(event, ConfigValue.ordered_sword_buy_problem);
                }
            }
            break;
        }

        if(event.getProblems().isEmpty() || removeItems.isEmpty())
            return;

        for(ItemStack itemStack : removeItems)
            pi.remove(itemStack);
    }
}