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

        final Player p = e.getPlayer();
        final PlayerInventory pi = p.getInventory();
        final boolean enabled = ServerManager.getSwordsToolsConfig().getBoolean("Replace-Sword-On-Buy.Enabled");
        final boolean allType = ServerManager.getSwordsToolsConfig().getBoolean("Replace-Sword-On-Buy.All-Type");

        if(e.getProblems().isEmpty() && enabled){
            // Checks if player bought a sword
            for(ShopProduct rawProduct:e.getItem().getProducts()){
                if(rawProduct instanceof ItemShopProduct){
                    final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                    for(ItemStack item:is){
                        if(item.getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(e.getItem().getDisplayName())){
                            //Clear Wooden Swords
                            if(allType){
                                for(ItemStack itemStack : pi) {
                                    if(itemStack != null && itemStack.getType().name().contains("SWORD")) {
                                            pi.remove(itemStack);
                                    }
                                }
                            }else{
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
}