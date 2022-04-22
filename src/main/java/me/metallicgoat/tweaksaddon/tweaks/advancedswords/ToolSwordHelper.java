package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class ToolSwordHelper {

    public static Material WOOD_SWORD;

    public static void load(){
        Material sword = Helper.get().getMaterialByName("WOODEN_SWORD");

        if(sword == null)
            sword = Material.AIR;

        WOOD_SWORD = sword;
    }

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

    // Some items may look like a sword, but not be one. Example: Special Items
    public static boolean isNotToIgnore(ItemStack itemStack){
        final ItemMeta meta = itemStack.getItemMeta();
        boolean isNotToIgnore = true;
        if(meta != null) {
            for(String s : ConfigValue.tools_swords_do_not_effect){
                if(s.equals(ChatColor.stripColor(meta.getDisplayName())) && !s.equals("")) {
                    isNotToIgnore = false;
                }
            }
        }
        return isNotToIgnore;
    }

    public static boolean isNotToIgnore(String name){
        boolean isNotToIgnore = true;
        for (String s : ConfigValue.tools_swords_do_not_effect){
            final String formatted = ChatColor.translateAlternateColorCodes('&', s);
            if(formatted.equals(name) && !s.equals("")){
                isNotToIgnore = false;
            }
        }
        return isNotToIgnore;
    }

    public static boolean doesInventoryContain(PlayerInventory playerInventory, String material){
        for(ItemStack itemStack:playerInventory){
            if(itemStack != null && itemStack.getType().name().contains(material)
                    && isNotToIgnore(itemStack.getItemMeta() != null ? itemStack.getItemMeta().getDisplayName():"NOTHING")) {
                return true;
            }
        }
        return false;
    }

    // Checks how many swords a player has
    public static int getSwordsAmount(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if(item != null && item.getType().name().endsWith("SWORD")
                    && ToolSwordHelper.isNotToIgnore(item)) {
                count++;
            }
        }
        return count;
    }

    // returns true if a player has a sword that is better than wood
    public static boolean hasBetterSword(Player player){
        final Inventory pi = player.getInventory();
        for(ItemStack itemStack : pi.getContents()){
            if(itemStack != null
                    && itemStack.getType().name().endsWith("SWORD")
                    && ToolSwordHelper.isNotToIgnore(itemStack)
                    && itemStack.getType() == ToolSwordHelper.WOOD_SWORD){

                return true;
            }
        }
        return false;
    }

    public static void addShopProblem(PlayerBuyInShopEvent event, String problem) {
        event.addProblem(new PlayerBuyInShopEvent.Problem() {
            @Override
            public Plugin getPlugin() {
                return MBedwarsTweaksPlugin.getInstance();
            }

            @Override
            public void handleNotification(PlayerBuyInShopEvent e) {
                e.getPlayer().sendMessage(Message.build(problem).done());
            }
        });
    }
}
