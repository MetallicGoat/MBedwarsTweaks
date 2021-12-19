package me.metallicgoat.MBedwarsTweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LockTeamChest implements Listener {
    @EventHandler
    public void onChestOpen(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        Block block = e.getClickedBlock();

        if(ServerManager.getConfig().getBoolean("Lock-Team-Chest")) {
            //Check if player is opening chest in an arena
            if (arena == null ||
                    arena.getStatus() != ArenaStatus.RUNNING ||
                    block == null ||
                    e.getAction() != Action.RIGHT_CLICK_BLOCK)
                return;

            if (block.getType() == Material.CHEST) {
                Team playerTeam = arena.getPlayerTeam(player);
                Team chestTeam = getChestTeam(arena, block);

                if (chestTeam != null && !arena.getPlayersInTeam(chestTeam).isEmpty() && chestTeam != playerTeam) {
                    String failOpen = ServerManager.getConfig().getString("Prevent-Chest-Open-Message");
                    player.sendMessage(Message.build(failOpen).placeholder("team", chestTeam.getDisplayName()).done());
                    e.setCancelled(true);
                }
            }
        }
    }

    private static Team getChestTeam(Arena arena, Block chest){
        double distance = ServerManager.getConfig().getDouble("Team-Chest-Distance");
        if(arena.getGameWorld() == chest.getWorld()) {
            for (Team team : arena.getEnabledTeams()) {
                XYZYP spawn = arena.getTeamSpawn(team);
                if (spawn != null) {
                    Location bukkitSpawn = spawn.toLocation(arena.getGameWorld());
                    if((distance * distance) >= bukkitSpawn.distanceSquared(chest.getLocation())){
                        return team;
                    }
                }
            }
        }
        return null;
    }
}
