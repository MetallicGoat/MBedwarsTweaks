package me.metallicgoat.MBedwarsTweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class OrderedSwordBuy implements Listener {
    @EventHandler
    public void onSwordBuy(PlayerBuyInShopEvent e){
        Player p = e.getPlayer();
        PlayerInventory pi = p.getInventory();
        if(swordBuy()) {
            for (ShopProduct rawProduct : e.getItem().getProducts()) {
                if (rawProduct instanceof ItemShopProduct) {
                    final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                    for (ItemStack item : is) {
                        if (item.getType().name().contains("SWORD") &&
                                ToolSwordHelper.isNotToIgnore(e.getItem().getDisplayName())) {
                            if (!isPurchasable(item, pi)) {
                                addShopProblem(e);
                            } else {
                                if (e.getProblems().isEmpty()) {
                                    clearOld(item.getType(), p);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void clearOld(Material tool, Player p){
        p.getInventory().forEach(itemStack -> {
            if(itemStack != null){
                if(itemStack.getType().name().contains("SWORD")
                        && ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())){
                    p.getInventory().remove(itemStack);
                }
            }
        });
    }

    private boolean isPurchasable(ItemStack product, PlayerInventory pi){
        if(pi.contains(product.getType())){
            return false;
        }

        for(ItemStack itemStack:pi){
            if(itemStack != null) {
                if (itemStack.getType().name().contains("SWORD")) {
                    if (ToolSwordHelper.getSwordToolLevel(itemStack.getType()) > ToolSwordHelper.getSwordToolLevel(product.getType())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    private void addShopProblem(PlayerBuyInShopEvent e){
        e.addProblem(new PlayerBuyInShopEvent.Problem() {
            @Override
            public Plugin getPlugin() {
                return Main.getInstance();
            }

            @Override
            public void handleNotification(PlayerBuyInShopEvent e) {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', getOrderedSwordBuyProblem()));
            }
        });
    }

    private boolean swordBuy() {
        return ServerManager.getSwordsToolsConfig().getBoolean("Ordered-Sword-Buy.Enabled");
    }

    private String getOrderedSwordBuyProblem() {
        return ServerManager.getSwordsToolsConfig().getString("Ordered-Sword-Buy.Problem");
    }
}