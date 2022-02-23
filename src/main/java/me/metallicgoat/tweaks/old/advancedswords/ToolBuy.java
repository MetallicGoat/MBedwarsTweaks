package me.metallicgoat.tweaks.old.advancedswords;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaks.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ToolBuy implements Listener {

    @EventHandler
    public void onToolBuy(PlayerBuyInShopEvent e) {
        Player p = e.getPlayer();
        Arena arena = e.getArena();
        BuyGroup group = e.getItem().getBuyGroup();

        //If enabled, and item has buy-group
        if(group != null
                && ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Tool-Replacement.Enabled")
                && e.getProblems().isEmpty()
                && group.getName().contains("axe")) {

            int currentLevel = arena.getBuyGroupLevel(p, e.getItem().getBuyGroup());
            if(group.getName().equalsIgnoreCase("pickaxe")){
                if(!ToolSwordHelper.doesInventoryContain(p.getInventory(), "PICKAXE")){
                    currentLevel = 0;
                }
            }else if(group.getName().equalsIgnoreCase("axe")){
                if(!ToolSwordHelper.doesInventoryContain(p.getInventory(), "_AXE")){
                    currentLevel = 0;
                }
            }

            //If getting higher tier
            if (e.getItem().getBuyGroupLevel() > currentLevel) {
                if (ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Tool-Replacement.Force-Ordered")) {
                    //current + 1 = buying level
                    if (e.getItem().getBuyGroupLevel() != currentLevel + 1) {
                        addShopProblem(e, ServerManager.getSwordsToolsConfig().getString("Advanced-Tool-Replacement.Force-Ordered-Problem"));
                    } else {
                        clearOld(ToolSwordHelper.getToolInShopProduct(e.getItem()), p);
                    }
                } else {
                    clearOld(ToolSwordHelper.getToolInShopProduct(e.getItem()), p);
                }
            //Lower or same tier
            } else {
                addShopProblem(e, ServerManager.getSwordsToolsConfig().getString("Advanced-Tool-Replacement.Problem"));
            }
        }
    }

    private void clearOld(Material tool, Player p) {
        boolean pickaxe = tool.name().contains("PICKAXE");
        for(ItemStack itemStack : p.getInventory()) {
            if (itemStack != null && itemStack.getType().name().contains("AXE")) {
                if ((itemStack.getType().name().contains("PICKAXE") && pickaxe) ||
                        (!itemStack.getType().name().contains("PICKAXE") && !pickaxe)) {
                    if (ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())) {
                        p.getInventory().remove(itemStack.getType());
                    }
                }
            }
        }
    }

    private void addShopProblem(PlayerBuyInShopEvent e, String problem) {
        e.addProblem(new PlayerBuyInShopEvent.Problem() {
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