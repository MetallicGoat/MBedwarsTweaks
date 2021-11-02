package me.metallicgoat.MBedwarsTweaks.tweaks.spawners;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.AddPlayerIssue;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerJoinArenaEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class GenTiers implements Listener {

    //TODO: Rewrite

    public static HashMap<Arena, String> nextTierMap = new HashMap<>();
    public static HashMap<Arena, Long> timeToNextUpdate = new HashMap<>();

    @EventHandler
    public void onGameStart(RoundStartEvent e){
        Arena arena = e.getArena();

        boolean enabled = plugin().getConfig().getBoolean("Gen-Tiers-Enabled");
        ConfigurationSection sect = ServerManager.getTiersConfig().getConfigurationSection("Gen-Tiers");
        if(enabled && sect != null) {
            List<String> tierOneSpawners = ServerManager.getConfig().getStringList("Tier-One-Titles.Spawners");
            String tierLevel = ServerManager.getConfig().getString("Tier-One-Titles.Tier-Name");
            if (ServerManager.getConfig().getBoolean("Gen-Tiers-Holos-Enabled")) {
                arena.getSpawners().forEach(spawner -> {
                    if (tierOneSpawners.contains(getItemType(spawner))) {
                        spawner.setOverridingHologramLines(formatHoloTiles(tierLevel, spawner).toArray(new String[0]));
                    }
                });
            }
            scheduleTier(arena, 0);
        }
    }

    private void scheduleTier(Arena arena, int key){
        BukkitScheduler scheduler = plugin().getServer().getScheduler();

        ConfigurationSection sect = ServerManager.getTiersConfig().getConfigurationSection("Gen-Tiers");
        assert sect != null;

        String[] orderedList = sect.getKeys(false).toArray(new String[0]);

        if(orderedList.length < key){
            return;
        }

        String section = orderedList[key];

        if(sect.contains(section)) {

            final String tierName = ServerManager.getTiersConfig().getString("Gen-Tiers." + section + ".TierName");
            final String tierLevel = ServerManager.getTiersConfig().getString("Gen-Tiers." + section + ".TierLevel");
            final long time = ServerManager.getTiersConfig().getLong("Gen-Tiers." + section + ".Time");
            final long speed = ServerManager.getTiersConfig().getLong("Gen-Tiers." + section + ".Speed");
            final String spawnerType = ServerManager.getTiersConfig().getString("Gen-Tiers." + section + ".Type");
            final String chat = ServerManager.getTiersConfig().getString("Gen-Tiers." + section + ".Chat");

            int newKey = key + 1;

            // Update Placeholder
            nextTierMap.remove(arena);
            nextTierMap.put(arena, tierName);

            timeToNextUpdate.remove(arena);
            timeToNextUpdate.put(arena, time * 20 * 60);

            if(!section.equalsIgnoreCase("game-over")) {
                if (section.equalsIgnoreCase("bed-break")) {
                    scheduler.scheduleSyncDelayedTask(plugin(), () -> {
                        //stay in scheduler
                        if (arena.getStatus() == ArenaStatus.RUNNING) {
                            scheduleTier(arena, newKey);
                            ScheduleBedBreak.breakArenaBeds(arena);
                        }
                    }, time * 20 * 60);

                } else {
                    scheduler.scheduleSyncDelayedTask(plugin(), () -> {
                        if (arena.getStatus() == ArenaStatus.RUNNING) {
                            scheduleTier(arena, newKey);
                            arena.broadcast(Message.build(chat));

                            for (Spawner s : arena.getSpawners()) {
                                if (getItemType(s).equalsIgnoreCase(spawnerType)) {
                                    if (ServerManager.getConfig().getBoolean("Gen-Tiers-Holos-Enabled")) {
                                        s.addDropDurationModifier("GEN_TIER_UPDATE", plugin(), SpawnerDurationModifier.Operation.SET, speed);
                                    }
                                    s.setOverridingHologramLines(formatHoloTiles(tierLevel, s).toArray(new String[0]));
                                }
                            }
                        } else {
                            nextTierMap.remove(arena);
                        }
                    }, time * 20 * 60);
                }
            }
        }
    }

    public static void startUpdatingTime(){
        BukkitScheduler scheduler = plugin().getServer().getScheduler();
        scheduler.runTaskTimer(plugin(),() -> {
            if(ServerManager.getConfig().getBoolean("Scoreboard-Updating")) {
                if (!timeToNextUpdate.isEmpty()) {
                    timeToNextUpdate.forEach((arena, integer) -> {
                        if (arena.getStatus() == ArenaStatus.RUNNING) {
                            timeToNextUpdate.replace(arena, integer, integer - 20);
                            if (((integer - 20) / 20) % 5 == 0) {
                                arena.updateScoreboard();
                            }
                        }
                    });
                }
            }
        }, 0L, 20L);
    }

    private String getItemType(Spawner s){
        for(ItemStack i : s.getDropType().getDroppingMaterials()){
            return i.getType().name();
        }
        return "";
    }

    public static String timeLeft(Arena arena) {

        int timeoutTicks = Math.toIntExact(timeToNextUpdate.get(arena));
        int timeoutSeconds = (timeoutTicks / 20);

        int minutes = (timeoutSeconds / 60) % 60;
        int seconds = timeoutSeconds % 60;

        System.out.println(minutes + ":" + seconds);

        if(minutes > 0 && seconds > 0) {
            if (seconds < 10) {
                return minutes + ":0" + seconds;
            } else {
                return minutes + ":" + seconds;
            }
        }else if(seconds == 0 && minutes > 0 ) {
            return minutes + ":00";
        } else {
            return "0:00";
        }
    }

    private List<String> formatHoloTiles(String tier, Spawner spawner){
        String spawnerName = spawner.getDropType().getConfigName();
        String colorCode = "&" + spawnerName.charAt(1);
        String strippedSpawnerName = spawnerName.substring(2);
        List<String> formatted = new ArrayList<>();
        ServerManager.getConfig().getStringList("Spawner-Title").forEach(s -> {
            String formattedString = s.replace("{tier}", tier)
                    .replace("{spawner-color}", colorCode)
                    .replace("{spawner}", strippedSpawnerName);
            formatted.add(formattedString);
        });
        return formatted;
    }

    private static Main plugin(){
        return Main.getInstance();
    }

}
