package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.TeamEliminateEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.Optional;

public class LootDropAtEliminatedTeamBase implements Listener {

    @EventHandler
    public void onEliminate(TeamEliminateEvent event){
        if (!MainConfig.personal_loot_drop || event.causesEnd())
            return;
        final Team team = event.getTeam();
        final Arena arena = event.getArena();
        final XYZYP teamLoc = arena.getTeamSpawn(team);
        final Inventory inventory = arena.getTeamPrivateInventory(team);
        final World gameWorld = arena.getGameWorld();
        if (gameWorld == null || inventory == null) {
            return;
        }
        Optional<Spawner> closestSpawner = arena.getSpawners().stream().min(Comparator.comparing(spawner1 -> spawner1.getLocation().distance(teamLoc)));
        if (inventory.getContents() == null || inventory.getContents().length == 0 || !closestSpawner.isPresent())
            return;
        final Location locationToDropItems = closestSpawner.get().getLocation().toLocation(gameWorld);
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null)
                continue;
            // Not checking game world as it's not null always.
            gameWorld.dropItemNaturally(locationToDropItems, itemStack);
        }
        if (MainConfig.strike_lighting_on_eliminated_base)
            gameWorld.strikeLightningEffect(locationToDropItems);
    }

}
