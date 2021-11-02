package me.metallicgoat.MBedwarsTweaks.advancedswords;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ToolBuy implements Listener {

    @EventHandler
    public void onToolBuy(PlayerBuyInShopEvent e) {
        Player p = e.getPlayer();
        Arena arena = e.getArena();

        //If enabled, and item has buy-group
        if(e.getItem().hasBuyGroup()
                && ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Tool-Replacement.Enabled")
                && e.getProblems().isEmpty()){

            BuyGroup group = e.getItem().getBuyGroup();

            //if proper buy-group
            assert group != null;
            if(group.getName().equalsIgnoreCase("axe")
                    || group.getName().equalsIgnoreCase("pickaxe")) {

                int currentLevel = arena.getBuyGroupLevel(p, e.getItem().getBuyGroup());
                if(group.getName().equalsIgnoreCase("pickaxe")){
                    if(!ToolSwordHelper.doesInventoryContain(p.getInventory(), "PICKAXE")){
                        currentLevel = 0;
                    }
                }else{
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
    }

    private void clearOld(Material tool, Player p) {
        boolean pickaxe = tool.name().contains("PICKAXE");
        p.getInventory().forEach(itemStack -> {
            if (itemStack != null && itemStack.getType().name().contains("AXE")) {
                if (itemStack.getType().name().contains("PICKAXE") && pickaxe) {
                    if (ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())) {
                        p.getInventory().remove(itemStack.getType());
                    }
                } else if (!itemStack.getType().name().contains("PICKAXE") && !pickaxe) {
                    if (ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())) {
                        p.getInventory().remove(itemStack.getType());
                    }
                }
            }
        });
    }

    private void addShopProblem(PlayerBuyInShopEvent e, String problem) {
        e.addProblem(new PlayerBuyInShopEvent.Problem() {
            @Override
            public Plugin getPlugin() {
                return Main.getInstance();
            }

            @Override
            public void handleNotification(PlayerBuyInShopEvent e) {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', problem));
            }
        });
    }
}