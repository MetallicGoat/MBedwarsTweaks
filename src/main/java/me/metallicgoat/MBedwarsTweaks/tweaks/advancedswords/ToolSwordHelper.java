package me.metallicgoat.MBedwarsTweaks.tweaks.advancedswords;

import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.concurrent.atomic.AtomicBoolean;

public class ToolSwordHelper {

    public static int getSwordLevel(String tool){
        if(tool.contains("WOOD")){
            return 1;
        }else if(tool.contains("STONE")){
            return 2;
        }else if(tool.contains("IRON")){
            return 3;
        }else if(tool.contains("GOLD")){
            return 4;
        }else if(tool.contains("DIAMOND")){
            return 5;
        }else if(tool.contains("NETHERITE")){
            return 6;
        }else{
            return 0;
        }
    }

    public static boolean doesShopProductContain(ShopItem shopItem, String material){
        for (ShopProduct rawProduct : shopItem.getProducts()) {
            if (rawProduct instanceof ItemShopProduct) {
                final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                for (ItemStack item : is) {
                    if (item.getType().name().contains(material) &&
                            isNotToIgnore(ChatColor.stripColor(shopItem.getDisplayName()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isNotToIgnore(String name){
        AtomicBoolean isNotToIgnore = new AtomicBoolean(true);
        ServerManager.getSwordsToolsConfig().getStringList("Do-Not-Effect").forEach(s -> {
            String formatted = ChatColor.translateAlternateColorCodes('&', s);
            if(formatted.equals(name) && !s.equals("")){
                isNotToIgnore.set(false);
            }
        });
        return isNotToIgnore.get();
    }

    public static boolean doesInventoryContain(PlayerInventory playerInventory, String material){
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        playerInventory.forEach(itemStack -> {
            if(itemStack.getType().name().contains(material)){
                atomicBoolean.set(true);
            }
        });
        return atomicBoolean.get();
    }
}
