package me.metallicgoat.MBedwarsTweaks.advancedswords;

import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.atomic.AtomicBoolean;

public class ToolSwordHelper {

    public static int getSwordToolLevel(Material tool){

        String toolName = tool.name();

        if(toolName.contains("WOOD")){
            return 1;
        }else if(toolName.contains("STONE")){
            return 2;
        }else if(toolName.contains("IRON")){
            return 3;
        }else if(toolName.contains("GOLD")){
            return 4;
        }else if(toolName.contains("DIAMOND")){
            return 5;
        }else if(toolName.contains("NETHERITE")){
            return 6;
        }else{
            return 0;
        }
    }

    public static String getMaterialFromLevel(int level){
        switch (level){
            case 1: return "WOOD";
            case 2: return "STONE";
            case 3: return "IRON";
            case 4: return "GOLD";
            case 5: return "DIAMOND";
            case 6: return "NETHERITE";
            default: return "AIR";
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
        AtomicBoolean isNotToIgnore = new AtomicBoolean(true);
        if(meta != null) {
            ServerManager.getSwordsToolsConfig().getStringList("Do-Not-Effect").forEach(s -> {
                if(s.equals(ChatColor.stripColor(meta.getDisplayName())) && !s.equals("")) {
                    isNotToIgnore.set(false);
                }
            });
        }
        return isNotToIgnore.get();
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
