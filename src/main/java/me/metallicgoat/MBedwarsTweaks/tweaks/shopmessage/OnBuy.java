package me.metallicgoat.MBedwarsTweaks.tweaks.shopmessage;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class OnBuy implements Listener {

    @EventHandler
    public void onBuyEvent(PlayerBuyInShopEvent e) {
        Player p = e.getPlayer();
        String product = e.getItem().getDisplayName();
        String message = ServerManager.getConfig().getString("BuyMessage.FinalKillMessage");
        boolean enabled = ServerManager.getConfig().getBoolean("BuyMessage.Enabled");

        String amount = Integer.toString(getAmount(e));
        String placeholdersInMessage = message.replace("%product%", product)
                .replace("%amount%", amount);
        String fullyFormattedMessage = ChatColor.translateAlternateColorCodes('&', placeholdersInMessage);
        if(e.getProblems().isEmpty()) {
            if(enabled) {
                p.sendMessage(fullyFormattedMessage);
            }
        }
    }

    private int getAmount(PlayerBuyInShopEvent e){
        int count = 0;
        for(ShopProduct rawProduct:e.getItem().getProducts()){
            if(rawProduct instanceof ItemShopProduct){
                final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                for(ItemStack item:is){
                    count = item.getAmount();
                    break;
                }
            }
        }
        return count;
    }
}