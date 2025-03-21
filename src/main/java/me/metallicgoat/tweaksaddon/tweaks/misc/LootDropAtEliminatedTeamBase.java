package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.TeamEliminateEvent;
import de.marcely.bedwars.api.event.player.PlayerKillPlayerEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.Optional;

public class LootDropAtEliminatedTeamBase implements Listener {

    @EventHandler
    public void onEliminate(TeamEliminateEvent event){
        if (!MainConfig.personal_team_loot_drop || event.causesEnd())
            return;
        final Team team = event.getTeam();
        final Arena arena = event.getArena();
        final Inventory inventory = arena.getTeamPrivateInventory(team);
        final World gameWorld = arena.getGameWorld();
        if (gameWorld == null || inventory == null) {
            return;
        }
        // Easy to call a function twice than writing same stuff twice
        dropItemsOnGen(arena, inventory, gameWorld, team);
    }

    @EventHandler
    public void onFinalKill(PlayerKillPlayerEvent event){
        if (!MainConfig.personal_loot_drop)
            return;
        final Arena arena = event.getArena();
        final Player player = event.getPlayer();
        // Checking for final kill.
        if (event.isFatalDeath()){
            final Team team = arena.getPlayerTeam(player);
            final Inventory inventory = arena.getPlayerPrivateInventory(player);
            if (inventory == null|| inventory.getContents() == null || inventory.getContents().length == 0)
                return;
            dropItemsOnGen(arena, inventory, arena.getGameWorld(), team);

        }
    }

    private void dropItemsOnGen(Arena arena, Inventory inventory, World gameWorld, Team team){
        Optional<Spawner> closestSpawner = arena.getSpawners().stream().min(Comparator.comparing(spawner1 -> spawner1.getLocation().distance(arena.getTeamSpawn(team))));
        if (inventory.getContents() == null || inventory.getContents().length == 0 || !closestSpawner.isPresent())
            return;
        final Location locationToDropItems = closestSpawner.get().getLocation().toLocation(gameWorld);
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || isBlocked(itemStack))
                continue;
            // Not checking game world as it's not null always.
            gameWorld.dropItemNaturally(locationToDropItems, itemStack);
        }
        if (MainConfig.personal_team_loot_drop_strike_lightning_enabled)
            gameWorld.strikeLightningEffect(locationToDropItems);
    }

    private boolean isBlocked(ItemStack itemStack){
        for (String personalLootBlockedItem : MainConfig.personal_loot_blocked_items) {
            final Material personalMaterial = Helper.get().getMaterialByName(personalLootBlockedItem);
            if (personalMaterial == null)
                continue;
            if (itemStack.getType() == personalMaterial)
                return true;
        }
        return false;
    }

}
