package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class BuyMessage implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBuyEvent(PlayerBuyInShopEvent event) {
        if(!ConfigValue.buy_message_enabled || !event.getProblems().isEmpty())
            return;

        final Player p = event.getPlayer();
        final String product = event.getItem().getDisplayName();
        final String amount = Integer.toString(getAmount(event.getItem()));
        final String message = Message.build(ConfigValue.buy_message)
                .placeholder("product", product)
                .placeholder("amount", amount).done();

        p.sendMessage(message);
    }

    private int getAmount(ShopItem item){
        int count = 0;
        for(ShopProduct rawProduct:item.getProducts()){
            if(rawProduct instanceof ItemShopProduct){
                final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                for(ItemStack itemStack:is){
                    count += itemStack.getAmount();
                }
            }
        }
        return count;
    }
}