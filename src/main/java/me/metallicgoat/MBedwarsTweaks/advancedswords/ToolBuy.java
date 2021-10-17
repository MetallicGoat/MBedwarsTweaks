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

public class ToolBuy implements Listener {
    @EventHandler
    public void onToolBuy(PlayerBuyInShopEvent e){
        Player p = e.getPlayer();
        PlayerInventory pi = p.getInventory();
        if(toolBuy()) {
            for (ShopProduct rawProduct : e.getItem().getProducts()) {
                if (rawProduct instanceof ItemShopProduct) {
                    final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                    for (ItemStack item : is) {
                        if (item.getType().name().contains("AXE") &&
                                ToolSwordHelper.isNotToIgnore(item)) {
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
        boolean pickaxe = tool.name().contains("PICKAXE");
        p.getInventory().forEach(itemStack -> {
            if(itemStack != null && itemStack.getType().name().contains("AXE")){
                if(itemStack.getType().name().contains("PICK")){
                    if(ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())){
                        p.getInventory().remove(itemStack.getType());
                    }
                }else if(!pickaxe){
                    if(ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())){
                        p.getInventory().remove(itemStack.getType());
                    }
                }
            }
        });
    }

    private boolean isPurchasable(ItemStack product, PlayerInventory pi){
        boolean pickaxe = product.getType().name().contains("PICKAXE");
        AtomicBoolean purchasable = new AtomicBoolean(true);

        if(pi.contains(product.getType())){
            return false;
        }
        pi.forEach(itemStack -> {
            if(itemStack != null) {
                if (itemStack.getType().name().contains("AXE")) {
                    if (itemStack.getType().name().contains("PICKAXE") && pickaxe) {
                        if (ToolSwordHelper.getSwordToolLevel(itemStack.getType()) > ToolSwordHelper.getSwordToolLevel(product.getType())) {
                            purchasable.set(false);
                        }
                    } else if (!itemStack.getType().name().contains("PICKAXE") && !pickaxe) {
                        if (ToolSwordHelper.getSwordToolLevel(itemStack.getType()) > ToolSwordHelper.getSwordToolLevel(product.getType())) {
                            purchasable.set(false);
                        }
                    }
                }
            }
        });

        return purchasable.get();
    }


    private void addShopProblem(PlayerBuyInShopEvent e){
        e.addProblem(new PlayerBuyInShopEvent.Problem() {
            @Override
            public Plugin getPlugin() {
                return Main.getInstance();
            }

            @Override
            public void handleNotification(PlayerBuyInShopEvent e) {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', toolBuyProblem()));
            }
        });
    }

    private boolean toolBuy() {
        return ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Tool-Replacement.Enabled");
    }

    private String toolBuyProblem() {
        return ServerManager.getSwordsToolsConfig().getString("Advanced-Tool-Replacement.Problem");
    }
}
