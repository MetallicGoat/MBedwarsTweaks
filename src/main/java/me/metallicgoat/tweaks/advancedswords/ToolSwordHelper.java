package me.metallicgoat.tweaks.advancedswords;

import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ToolSwordHelper {

    public static int getSwordToolLevel(Material tool){

        switch(tool.name()){
            case "WOOD":
                return 1;
            case "STONE":
                return 2;
            case "IRON":
                return 3;
            case "GOLD":
                return 4;
            case "DIAMOND":
                return 5;
            case "NETHERITE":
                return 6;
            default:
                return 0;
        }
    }


    public static Material getToolInShopProduct(ShopItem shopItem){
        for (ShopProduct rawProduct : shopItem.getProducts()) {
            if (rawProduct instanceof ItemShopProduct) {
                final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                for (ItemStack item : is) {
                    if (item.getType().name().contains("AXE") &&
                            isNotToIgnore(ChatColor.stripColor(shopItem.getDisplayName()))) {
                        return item.getType();
                    }
                }
            }
        }
        return Material.AIR;
    }

    public static boolean isNotToIgnore(ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        boolean isNotToIgnore = true;
        if(meta != null) {
            for(String s : ServerManager.getSwordsToolsConfig().getStringList("Do-Not-Effect")){
                if(s.equals(ChatColor.stripColor(meta.getDisplayName())) && !s.equals("")) {
                    isNotToIgnore = false;
                }
            }
        }
        return isNotToIgnore;
    }

    public static boolean isNotToIgnore(String name){
        boolean isNotToIgnore = true;
        for (String s : ServerManager.getSwordsToolsConfig().getStringList("Do-Not-Effect")){
            String formatted = ChatColor.translateAlternateColorCodes('&', s);
            if(formatted.equals(name) && !s.equals("")){
                isNotToIgnore = false;
            }
        }
        return isNotToIgnore;
    }

    public static boolean doesInventoryContain(PlayerInventory playerInventory, String material){
        for(ItemStack itemStack:playerInventory){
            if(itemStack != null) {
                if (itemStack.getType().name().contains(material)
                        && isNotToIgnore(itemStack.getItemMeta() != null ? itemStack.getItemMeta().getDisplayName():"NOTHING")) {
                    return true;
                }
            }
        }
        return false;
    }
}
