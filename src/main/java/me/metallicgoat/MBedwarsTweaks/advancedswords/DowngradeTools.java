package me.metallicgoat.MBedwarsTweaks.advancedswords;

import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import me.metallicgoat.MBedwarsTweaks.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Optional;

public class DowngradeTools implements Listener {

    private final HashMap<Player, Integer> playerAxe = new HashMap<>();
    private final HashMap<Player, Integer> playerPickaxe = new HashMap<>();

    @EventHandler
    public void onGameStart(RoundStartEvent e){
        //reset tool level on round start
        for (Player player : e.getArena().getPlayers()){
            playerAxe.put(player, 0);
            playerPickaxe.put(player, 0);
        }
    }

    @EventHandler
    public void onShopBuy(PlayerBuyInShopEvent e){
        ShopItem shopItem = e.getItem();

        //Check if enabled, and using advanced tool replacement
        if(ServerManager.getSwordsToolsConfig().getBoolean("Degraded-Tool-BuyGroups.Enabled")
                && ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Sword-Drop.Enabled")){
            if(ToolSwordHelper.doesShopProductContain(shopItem, "AXE")){

                //Get tool level
                int itemLevel = ToolSwordHelper.getSwordToolLevel(ToolSwordHelper.getToolInShopProduct(shopItem));

                if(ToolSwordHelper.doesShopProductContain(shopItem, "PICKAXE")){
                    //PICKAXE
                    playerPickaxe.put(e.getPlayer(), itemLevel);
                }else{
                    //AXE
                    playerAxe.put(e.getPlayer(), itemLevel);
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerIngameRespawnEvent e) {
        Player player = e.getPlayer();
        if(ServerManager.getSwordsToolsConfig().getBoolean("Degraded-Tool-BuyGroups.Enabled")
                && ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Sword-Drop.Enabled")) {

            //reduce player pickaxe by one if necessary
            if (playerPickaxe.get(player) > 1) {
                int currentLevel = playerPickaxe.get(player) - 1;
                //Check to make sure tool is offered
                while (!ServerManager.getSwordsToolsConfig().getStringList("Degraded-Tool-BuyGroups.Pickaxe-Types").contains(ToolSwordHelper.getMaterialFromLevel(currentLevel))
                        && currentLevel >= 1) {
                    currentLevel--;
                }
                playerPickaxe.replace(player, currentLevel);
            }
            //reduce player axe by one if necessary
            if (playerAxe.get(player) > 1) {
                int currentLevel = playerAxe.get(player) - 1;
                //Check to make sure tool is offered
                while (!ServerManager.getSwordsToolsConfig().getStringList("Degraded-Tool-BuyGroups.Axe-Types").contains(ToolSwordHelper.getMaterialFromLevel(currentLevel))
                        && currentLevel >= 1) {
                    currentLevel--;
                }
                playerAxe.replace(player, currentLevel);
            }

            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskLater(Main.getInstance(), () -> {
                String pickaxe = ToolSwordHelper.getMaterialFromLevel(playerPickaxe.get(player));
                String axe = ToolSwordHelper.getMaterialFromLevel(playerAxe.get(player));

                //Check to make sure player has pickaxe
                if (!pickaxe.equals("AIR")) {
                    Optional<XMaterial> material = XMaterial.matchXMaterial(pickaxe + "_PICKAXE");
                    material.ifPresent(XMaterial -> player.getInventory().addItem(XMaterial.parseItem()));
                }
                //Check to make sure player has axe
                if (!axe.equals("AIR")) {
                    Optional<XMaterial> material = XMaterial.matchXMaterial(axe + "_AXE");
                    material.ifPresent(XMaterial -> player.getInventory().addItem(XMaterial.parseItem()));
                }
            }, 2L);
        }
    }
}
