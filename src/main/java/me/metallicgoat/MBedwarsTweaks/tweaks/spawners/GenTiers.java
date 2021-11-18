package me.metallicgoat.MBedwarsTweaks.tweaks.spawners;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GenTiers implements Listener {

    //TODO: Actually end game

    public static ConfigurationSection section;

    public static HashMap<Arena, String> nextTierMap = new HashMap<>();
    public static HashMap<Arena, Long> timeToNextUpdate = new HashMap<>();
    private final HashMap<Arena, BukkitTask> tasksToKill = new HashMap<>();
    private BukkitTask placeHolderTask = null;

    @EventHandler
    public void onGameStart(RoundStartEvent e){
        Arena arena = e.getArena();
        boolean enabled = plugin().getConfig().getBoolean("Gen-Tiers-Enabled");

        if(enabled) {
            //Start updating placeholders
            if(placeHolderTask == null){
                placeHolderTask = startUpdatingTime();
            }

            List<String> tierOneSpawners = ServerManager.getConfig().getStringList("Tier-One-Titles.Spawners");
            String tierLevel = ServerManager.getConfig().getString("Tier-One-Titles.Tier-Name");
            if (ServerManager.getConfig().getBoolean("Gen-Tiers-Holos-Enabled")) {
                //Add custom Holo titles
                for (Spawner spawner:arena.getSpawners()){
                    if (tierOneSpawners.contains(getItemType(spawner))) {
                        spawner.setOverridingHologramLines(formatHoloTiles(tierLevel, spawner).toArray(new String[0]));
                    }
                }
            }
            scheduleTier(arena, 0);
        }
    }

    @EventHandler
    public void onGameStop(RoundEndEvent event){
        //Kill Gen Tiers on round end
        BukkitTask task = tasksToKill.get(event.getArena());
        if(task != null) {
            task.cancel();
            tasksToKill.remove(event.getArena());
        }

        //Dont kill task if
        for(Arena arena:BedwarsAPI.getGameAPI().getArenas()){
            if(arena.getStatus() == ArenaStatus.RUNNING){
                return;
            }
        }

        //Kill task
        if(placeHolderTask != null){
            placeHolderTask.cancel();
            placeHolderTask = null;
        }
    }

    private void scheduleTier(Arena arena, int key){
        BukkitScheduler scheduler = plugin().getServer().getScheduler();

        //Array of all tier config section names
        String[] orderedList = section.getKeys(false).toArray(new String[0]);

        //Check if gone through all tiers
        if(orderedList.length < key){
            return;
        }

        //Name of current Tiers' config section name
        String group = orderedList[key];

        final String tierName = ServerManager.getTiersConfig().getString("Gen-Tiers." + group + ".TierName");
        final String tierLevel = ServerManager.getTiersConfig().getString("Gen-Tiers." + group + ".TierLevel");
        final long time = ServerManager.getTiersConfig().getLong("Gen-Tiers." + group + ".Time");
        final long speed = ServerManager.getTiersConfig().getLong("Gen-Tiers." + group + ".Speed");
        final String spawnerType = ServerManager.getTiersConfig().getString("Gen-Tiers." + group + ".Type");
        final String chat = ServerManager.getTiersConfig().getString("Gen-Tiers." + group + ".Chat");

        int newKey = key + 1;

        // Update Placeholder
        nextTierMap.put(arena, tierName);
        timeToNextUpdate.put(arena, time * 20 * 60);

        //Kill previous task if running for some reason
        BukkitTask task = tasksToKill.get(arena);
        if(task != null)
            task.cancel();

        switch (group.toLowerCase()){
            case "game-over": break;
            case "bed-break":
                tasksToKill.put(arena, scheduler.runTaskLater(plugin(), () -> {
                    if (arena.getStatus() == ArenaStatus.RUNNING) {
                        //Break beds, start next tier
                        scheduleTier(arena, newKey);
                        ScheduleBedBreak.breakArenaBeds(arena);
                    }
                }, time * 20 * 60));
                break;
            default:
                tasksToKill.put(arena, scheduler.runTaskLater(plugin(), () -> {
                    if (arena.getStatus() == ArenaStatus.RUNNING) {
                        scheduleTier(arena, newKey);
                        arena.broadcast(Message.build(chat));

                        //For all spawners
                        for (Spawner s : arena.getSpawners()) {
                            if (getItemType(s).equalsIgnoreCase(spawnerType)) {
                                //Set drop time
                                s.addDropDurationModifier("GEN_TIER_UPDATE", plugin(), SpawnerDurationModifier.Operation.SET, speed);
                                //Add custom Holo tiles
                                if (ServerManager.getConfig().getBoolean("Gen-Tiers-Holos-Enabled")) {
                                    s.setOverridingHologramLines(formatHoloTiles(tierLevel, s).toArray(new String[0]));
                                }
                            }
                        }
                    } else {
                        nextTierMap.remove(arena);
                    }
                }, time * 20 * 60));
                break;
        }
    }

    private static BukkitTask startUpdatingTime(){
        BukkitScheduler scheduler = plugin().getServer().getScheduler();
        boolean scoreBoardUpdating = ServerManager.getConfig().getBoolean("Scoreboard-Updating");
        int scoreBoardUpdatingInterval = ServerManager.getConfig().getInt("Scoreboard-Updating-Interval");

        return scheduler.runTaskTimer(plugin(),() -> {
            if(scoreBoardUpdating) {
                if (!timeToNextUpdate.isEmpty()) {
                    timeToNextUpdate.forEach((arena, integer) -> {
                        if (arena.getStatus() == ArenaStatus.RUNNING) {
                            timeToNextUpdate.replace(arena, integer, integer - 20);
                            if (((integer - 20) / 20) % scoreBoardUpdatingInterval == 0) {
                                arena.updateScoreboard();
                            }
                        }
                    });
                }
            }
        }, 0L, 20L);
    }

    //Get spawner dropping material
    private String getItemType(Spawner s){
        for(ItemStack i : s.getDropType().getDroppingMaterials()){
            return i.getType().name();
        }
        return "";
    }

    //Format time for placeholder
    public static String timeLeft(Arena arena) {

        int timeoutTicks = Math.toIntExact(timeToNextUpdate.get(arena));
        int timeoutSeconds = (timeoutTicks / 20);

        int minutes = (timeoutSeconds / 60) % 60;
        int seconds = timeoutSeconds % 60;

        if(minutes + seconds > 0) {
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

    //Format custom holo titles
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