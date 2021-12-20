package me.metallicgoat.tweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.tweaks.utils.ServerManager;
import me.metallicgoat.tweaks.utils.XSeries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ReplaceSwordOnBuy implements Listener {
    @EventHandler
    public void onSwordBuy(PlayerBuyInShopEvent e){
        Player p = e.getPlayer();
        PlayerInventory pi = p.getInventory();
        if(e.getProblems().isEmpty() && replace()){
            // Checks if player bought a sword
            for(ShopProduct rawProduct:e.getItem().getProducts()){
                if(rawProduct instanceof ItemShopProduct){
                    final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                    for(ItemStack item:is){
                        if(item.getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(e.getItem().getDisplayName())){
                            //Clear Wooden Swords
                            if(allType()){
                                pi.forEach(itemStack -> {
                                    if(itemStack != null) {
                                        if (itemStack.getType().name().contains("SWORD")) {
                                            pi.remove(itemStack);
                                        }
                                    }
                                });
                            }else {
                                assert XMaterial.WOODEN_SWORD.parseMaterial() != null;
                                pi.remove(XMaterial.WOODEN_SWORD.parseMaterial());
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    private boolean replace() {
        return ServerManager.getSwordsToolsConfig().getBoolean("Replace-Sword-On-Buy.Enabled");
    }
    private boolean allType() {
        return ServerManager.getSwordsToolsConfig().getBoolean("Replace-Sword-On-Buy.All-Type");
    }
}