package me.metallicgoat.MBedwarsTweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;

public class DowngradeToolsV2 implements Listener {

    @EventHandler
    public void onRespawn(PlayerIngameRespawnEvent e){
        Player player = e.getPlayer();
        Arena arena = e.getArena();

        Collection<BuyGroup> buyGroups = BedwarsAPI.getGameAPI().getBuyGroups();
        for(BuyGroup buyGroup : buyGroups){
            if(buyGroup.getName().equals("pickaxe")){
                int level = arena.getBuyGroupLevel(player, buyGroup);
                if(level > 1){
                    arena.setBuyGroupLevel(player, buyGroup, level - 1);
                }
            }
        }
    }
}
