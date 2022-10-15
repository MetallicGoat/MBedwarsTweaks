package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ToolBuy implements Listener {

    @EventHandler
    public void onToolBuy(PlayerBuyInShopEvent event) {
        if(!ConfigValue.advanced_tool_replacement_enabled)
            return;

        final BuyGroup group = event.getItem().getBuyGroup();

        //If enabled, and item has buy-group
        if(group != null
                && event.getProblems().isEmpty()
                && group.getName().contains("axe")) {

            final Player player = event.getPlayer();
            final Arena arena = event.getArena();
            int currentLevel = arena.getBuyGroupLevel(player, event.getItem().getBuyGroup());

            if(group.getName().equalsIgnoreCase("pickaxe")){
                if(!ToolSwordHelper.doesInventoryContain(player.getInventory(), "_PICKAXE")){
                    currentLevel = 0;
                }
            }else if(group.getName().equalsIgnoreCase("axe")){
                if(!ToolSwordHelper.doesInventoryContain(player.getInventory(), "_AXE")){
                    currentLevel = 0;
                }
            }

            //If getting higher tier
            if (event.getItem().getBuyGroupLevel() > currentLevel) {
                if (ConfigValue.advanced_tool_replacement_force_ordered) {
                    //current + 1 = buying level
                    if (event.getItem().getBuyGroupLevel() != currentLevel + 1) {
                        ToolSwordHelper.addShopProblem(event, ConfigValue.advanced_tool_replacement_force_ordered_problem);
                    } else {
                        clearOld(ToolSwordHelper.getToolInShopProduct(event.getItem()), player);
                    }
                } else {
                    clearOld(ToolSwordHelper.getToolInShopProduct(event.getItem()), player);
                }
            //Lower or same tier
            } else {
                ToolSwordHelper.addShopProblem(event, ConfigValue.advanced_tool_replacement_regular_problem);
            }
        }
    }

    private void clearOld(Material tool, Player p) {
        boolean pickaxe = tool.name().endsWith("PICKAXE");
        for(ItemStack itemStack : p.getInventory()) {
            if (itemStack != null && itemStack.getType().name().endsWith("AXE")) {
                if ((itemStack.getType().name().endsWith("PICKAXE") && pickaxe) ||
                        (!itemStack.getType().name().endsWith("PICKAXE") && !pickaxe)) {
                    if (ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())) {
                        p.getInventory().remove(itemStack.getType());
                    }
                }
            }
        }
    }
}