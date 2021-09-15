package me.metallicgoat.MBedwarsTweaks.tweaks.messages;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class BuyMessage implements Listener {

    @EventHandler
    public void onBuyEvent(PlayerBuyInShopEvent e) {
        Player p = e.getPlayer();
        String product = e.getItem().getDisplayName();
        String message = ServerManager.getConfig().getString("Buy-Message.Message");
        boolean enabled = ServerManager.getConfig().getBoolean("Buy-Message.Enabled");

        String amount = Integer.toString(getAmount(e));

        if(enabled && message != null) {
            if(e.getProblems().isEmpty()) {
                String placeholdersInMessage = Message.build(message).placeholder("product", product)
                        .placeholder("amount", amount).done();
                String fullyFormattedMessage = ChatColor.translateAlternateColorCodes('&', placeholdersInMessage);
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