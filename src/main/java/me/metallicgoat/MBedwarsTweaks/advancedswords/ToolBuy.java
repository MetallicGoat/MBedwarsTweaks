package me.metallicgoat.MBedwarsTweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class ToolBuy implements Listener {

    @EventHandler
    public void onToolBuy(PlayerBuyInShopEvent e){
        Player p = e.getPlayer();
        PlayerInventory pi = p.getInventory();
        if(toolBuy()) {
            for (ShopProduct rawProduct : e.getItem().getProducts()) {
                if (rawProduct instanceof ItemShopProduct) {
                    final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                    for (ItemStack item : is) {
                        if (item.getType().name().contains("AXE") &&
                                ToolSwordHelper.isNotToIgnore(item)) {
                            if (isPurchasable(e, item, pi)) {
                                if (e.getProblems().isEmpty()) {
                                    clearOld(item.getType(), p);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void clearOld(Material tool, Player p){
        boolean pickaxe = tool.name().contains("PICKAXE");
        p.getInventory().forEach(itemStack -> {
            if(itemStack != null && itemStack.getType().name().contains("AXE")){
                if(itemStack.getType().name().contains("PICKAXE") && pickaxe){
                    if(ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())){
                        p.getInventory().remove(itemStack.getType());
                    }
                }else if(!itemStack.getType().name().contains("PICKAXE") && !pickaxe){
                    if(ToolSwordHelper.getSwordToolLevel(tool) > ToolSwordHelper.getSwordToolLevel(itemStack.getType())){
                        p.getInventory().remove(itemStack.getType());
                    }
                }
            }
        });
    }

    private boolean isPurchasable(PlayerBuyInShopEvent e,ItemStack product, PlayerInventory pi){

        boolean pickaxe = product.getType().name().contains("PICKAXE");
        ItemStack tool = null;

        if(pi.contains(product.getType())){
            addShopProblem(e, toolBuyProblem());
            return false;
        }

        //First tier
        if((!ToolSwordHelper.doesInventoryContain(pi, "PICKAXE") && product.getType().name().contains("PICKAXE"))
                || (!ToolSwordHelper.doesInventoryContain(pi, "AXE") && product.getType().name().contains("AXE"))) {
            if (forceOrderedBuy() && ToolSwordHelper.getSwordToolLevel(product.getType()) > 1) {
                addShopProblem(e, forceOrderedToolBuyProblem());
                return false;
            }
        }

        //check inventory for tool
        for(ItemStack itemStack:pi){
            if(itemStack != null) {
                if ((itemStack.getType().name().contains("PICKAXE") && pickaxe)
                        || (itemStack.getType().name().contains("AXE") && !pickaxe)) {
                    tool = itemStack;
                    break;
                }
            }
        }

        if(tool != null) {
            //Buying lower tier
            if (ToolSwordHelper.getSwordToolLevel(tool.getType()) > ToolSwordHelper.getSwordToolLevel(product.getType())) {
                addShopProblem(e, toolBuyProblem());
                return false;
            }

            //Skipping tier
            if (forceOrderedBuy()){
                int currentLevel = ToolSwordHelper.getSwordToolLevel(tool.getType());

                if(pickaxe && tool.getType().name().contains("PICKAXE")) {
                    while(!ServerManager.getSwordsToolsConfig().getStringList("Tools-Sold.Pickaxe-Types").contains(ToolSwordHelper.getMaterialFromLevel(currentLevel + 1))){
                        currentLevel++;
                    }
                }else if(!pickaxe && !tool.getType().name().contains("PICKAXE")){
                    while(!ServerManager.getSwordsToolsConfig().getStringList("Tools-Sold.Axe-Types").contains(ToolSwordHelper.getMaterialFromLevel(currentLevel + 1))){
                        currentLevel++;
                    }
                }

                if (ToolSwordHelper.getSwordToolLevel(product.getType()) - currentLevel > 1) {
                    addShopProblem(e, forceOrderedToolBuyProblem());
                    return false;
                }
            }
        }
        return true;
    }


    private void addShopProblem(PlayerBuyInShopEvent e, String problem){
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

    private boolean toolBuy() {
        return ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Tool-Replacement.Enabled");
    }

    private boolean forceOrderedBuy() {
        return ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Tool-Replacement.Force-Ordered");
    }

    private String toolBuyProblem() {
        return ServerManager.getSwordsToolsConfig().getString("Advanced-Tool-Replacement.Problem");
    }

    private String forceOrderedToolBuyProblem() {
        return ServerManager.getSwordsToolsConfig().getString("Advanced-Tool-Replacement.Force-Ordered-Problem");
    }
}
