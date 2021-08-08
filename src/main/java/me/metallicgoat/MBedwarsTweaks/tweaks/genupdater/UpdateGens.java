package me.metallicgoat.MBedwarsTweaks.tweaks.genupdater;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class UpdateGens implements Listener {


    @EventHandler
    public void onGameStart(RoundStartEvent e){
        Arena arena = e.getArena();

        boolean enabled = plugin().getConfig().getBoolean("Gen-Tiers-Enabled");

        if(enabled){
            final ConfigurationSection sect = ServerManager.getTiersConfig().getConfigurationSection("Gen-Tiers");
            sect.getKeys(false).forEach(key ->  {
                final long time = ServerManager.getTiersConfig().getLong("Gen-Tiers." + key + ".Time") * 20 * 60;
                final long speed = ServerManager.getTiersConfig().getLong("Gen-Tiers." + key + ".Speed");
                final String type = ServerManager.getTiersConfig().getString("Gen-Tiers." + key + ".Type");
                final String chat = ServerManager.getTiersConfig().getString("Gen-Tiers." + key + ".Chat");

                scheduleTier(arena, chat, speed, time, type);

            });
        }
    }

    private void scheduleTier(Arena arena, String chat, long speed, long time, String spawnerType){
        BukkitScheduler scheduler = plugin().getServer().getScheduler();

        scheduler.scheduleSyncDelayedTask(plugin(), () -> {
            if(arena.getStatus() == ArenaStatus.RUNNING) {
                for (Spawner s : arena.getSpawners()) {
                    if (getItemType(s).equalsIgnoreCase(spawnerType)) {
                        s.addDropDurationModifier("tierUpdate", plugin(), SpawnerDurationModifier.Operation.SET, speed);
                    }
                }
                arena.getPlayers().forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', chat)));
            }
        }, time);
    }

    private String getItemType(Spawner s){
        for(ItemStack i : s.getDropType().getDroppingMaterials()){
            return i.getType().name();
        }
        return "";
    }

    private static Main plugin(){
        return Main.getInstance();
    }

}
