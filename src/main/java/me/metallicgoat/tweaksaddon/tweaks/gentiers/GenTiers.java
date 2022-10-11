package me.metallicgoat.tweaksaddon.tweaks.gentiers;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GenTiers implements Listener {

    public static HashMap<Arena, String> nextTierMap = new HashMap<>();
    public static HashMap<Arena, Long> timeToNextUpdate = new HashMap<>();
    private final HashMap<Arena, BukkitTask> tasksToKill = new HashMap<>();
    private BukkitTask placeHolderTask = null;

    @EventHandler
    public void onGameStart(RoundStartEvent event) {

        if (!ConfigValue.gen_tiers_enabled)
            return;

        // Start updating placeholders
        if (placeHolderTask == null)
            placeHolderTask = startUpdatingTime();

        final Arena arena = event.getArena();

        if (ConfigValue.gen_tiers_custom_holo_enabled) {

            // Add custom Holo titles
            for (Spawner spawner : arena.getSpawners()) {
                if(ConfigValue.gen_tiers_start_spawners.contains(spawner.getDropType())) {
                    spawner.setOverridingHologramLines(formatHoloTiles(ConfigValue.gen_tiers_start_tier, spawner).toArray(new String[0]));
                }
            }
        }

        scheduleTier(arena, 1);
    }

    @EventHandler
    public void onGameStop(RoundEndEvent event) {

        // Kill Gen Tiers on round end
        final BukkitTask task = tasksToKill.get(event.getArena());

        if (task != null) {
            task.cancel();
            tasksToKill.remove(event.getArena());
        }

        // Dont kill task if
        for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
            if (arena.getStatus() == ArenaStatus.RUNNING) {
                return;
            }
        }

        // Kill task
        if (placeHolderTask != null) {
            placeHolderTask.cancel();
            placeHolderTask = null;
        }
    }

    private void scheduleTier(Arena arena, int key) {

        // Check if tier exists
        if (ConfigValue.gen_tier_levels.get(key) == null)
            return;

        final GenTierLevel currentLevel = ConfigValue.gen_tier_levels.get(key);

        int nextTierLevel = key + 1;

        // Update Placeholder
        nextTierMap.put(arena, currentLevel.getTierName());
        timeToNextUpdate.put(arena, currentLevel.getTime() * 20 * 60);

        // Kill previous task if running for some reason
        BukkitTask task = tasksToKill.get(arena);
        if (task != null)
            task.cancel();

        switch (currentLevel.getAction()) {
            case GAME_OVER: {
                playTierSound(arena, currentLevel);
                arena.setIngameTimeRemaining((int) (currentLevel.getTime() * 60));
                break;
            }

            case BED_DESTROY: {
                tasksToKill.put(arena, Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
                    if (arena.getStatus() == ArenaStatus.RUNNING) {
                        // Break beds, start next tier
                        playTierSound(arena, currentLevel);
                        scheduleTier(arena, nextTierLevel);
                        BedBreakTier.breakArenaBeds(arena, currentLevel.getTierName());
                    }
                }, currentLevel.getTime() * 20 * 60));
                break;
            }

            case GEN_UPGRADE: {
                tasksToKill.put(arena, Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
                    if (arena.getStatus() == ArenaStatus.RUNNING) {

                        scheduleTier(arena, nextTierLevel);
                        arena.broadcast(Message.build(currentLevel.getEarnMessage()));

                        playTierSound(arena, currentLevel);
                        // For all spawners
                        for (Spawner s : arena.getSpawners()) {
                            if (currentLevel.getType() != null && s.getDropType() == currentLevel.getType()) {
                                // Set drop time
                                s.addDropDurationModifier("GEN_UPGRADE", MBedwarsTweaksPlugin.getInstance(), SpawnerDurationModifier.Operation.SET, currentLevel.getSpeed());
                                // Add custom Holo tiles
                                if (ConfigValue.gen_tiers_custom_holo_enabled) {
                                    s.setOverridingHologramLines(formatHoloTiles(currentLevel.getTierLevel(), s).toArray(new String[0]));
                                }
                            }
                        }
                    } else {
                        nextTierMap.remove(arena);
                    }
                }, currentLevel.getTime() * 20 * 60));
                break;
            }
        }
    }

    // TODO improve (why is this a part of gen-tier?) (shit code)
    private static BukkitTask startUpdatingTime() {
        return Bukkit.getServer().getScheduler().runTaskTimer(MBedwarsTweaksPlugin.getInstance(), () -> {
            if (timeToNextUpdate.isEmpty())
                return;

            timeToNextUpdate.forEach((arena, integer) -> {

                if (arena.getStatus() == ArenaStatus.RUNNING) {

                    if (MBedwarsTweaksPlugin.papiEnabled)
                        timeToNextUpdate.replace(arena, integer, integer - 20);

                }
            });
        }, 0L, 20L);
    }

    // Format time for placeholder
    public static String[] timeLeft(Arena arena) {
        final int timeoutTicks = timeToNextUpdate.getOrDefault(arena, 0L).intValue();
        final int timeoutSeconds = (timeoutTicks / 20);

        final int minutes = (timeoutSeconds / 60) % 60;
        final int seconds = timeoutSeconds % 60;

        return Util.formatMinSec(minutes, seconds);
    }

    // Format custom holo titles
    private List<String> formatHoloTiles(String tier, Spawner spawner) {
        final String spawnerName = spawner.getDropType().getConfigName();
        final String colorCode = "&" + spawnerName.charAt(1);
        final String strippedSpawnerName = spawnerName.substring(2);
        final List<String> formatted = new ArrayList<>();

        // Dont use placeholder, use REPLACE
        for(String string : ConfigValue.gen_tiers_spawner_holo_titles){
            final String formattedString = string
                    .replace("{tier}", tier)
                    .replace("{spawner-color}", colorCode)
                    .replace("{spawner}", strippedSpawnerName);

            formatted.add(ChatColor.translateAlternateColorCodes('&', formattedString));
        }

        return formatted;
    }

    private void playTierSound(Arena arena, GenTierLevel level){
        final Sound sound = level.getEarnSound();

        if(sound == null || arena == null)
            return;

        for(Player p : arena.getPlayers())
            p.playSound(p.getLocation(), sound, 1F, 1F);
    }
}