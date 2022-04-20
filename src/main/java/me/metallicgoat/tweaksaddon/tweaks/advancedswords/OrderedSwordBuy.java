package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class OrderedSwordBuy implements Listener {
    @EventHandler
    public void onSwordBuy(PlayerBuyInShopEvent event) {

        if (!ConfigValue.ordered_sword_buy_enabled || !event.getProblems().isEmpty())
            return;

        final Player player = event.getPlayer();
        final PlayerInventory pi = player.getInventory();

        for (ShopProduct rawProduct : event.getItem().getProducts()) {
            if (rawProduct instanceof ItemShopProduct) {
                final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                for (ItemStack item : is) {
                    if (item.getType().name().endsWith("SWORD") &&
                            ToolSwordHelper.isNotToIgnore(event.getItem().getDisplayName())) {

                        if (!isPurchasable(item, pi))
                            addShopProblem(event);
                        else
                            clearOld(item.getType(), player);
                    }
                }
            }
        }
    }

    private void clearOld(Material tool, Player player) {
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null && itemStack.getType().name().endsWith("SWORD")
                    && ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())) {
                player.getInventory().remove(itemStack);
            }
        }
    }

    private boolean isPurchasable(ItemStack product, PlayerInventory pi) {
        if (pi.contains(product.getType()))
            return false;

        for (ItemStack itemStack : pi) {
            if (itemStack != null && itemStack.getType().name().endsWith("SWORD")
                    && ToolSwordHelper.getSwordToolLevel(itemStack.getType()) > ToolSwordHelper.getSwordToolLevel(product.getType())) {
                return false;
            }
        }
        return true;
    }


    private void addShopProblem(PlayerBuyInShopEvent event) {
        event.addProblem(new PlayerBuyInShopEvent.Problem() {
            @Override
            public Plugin getPlugin() {
                return MBedwarsTweaksPlugin.getInstance();
            }

            @Override
            public void handleNotification(PlayerBuyInShopEvent e) {
                e.getPlayer().sendMessage(Message.build(ConfigValue.ordered_sword_buy_problem).done());
            }
        });
    }
}