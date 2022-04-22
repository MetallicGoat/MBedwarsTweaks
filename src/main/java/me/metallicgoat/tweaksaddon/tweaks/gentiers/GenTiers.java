package me.metallicgoat.tweaksaddon.tweaks.gentiers;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.AA_old.tweaks.spawners.ScheduleBedBreak;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GenTiers implements Listener {

    public static ConfigurationSection section;

    public static HashMap<Arena, String> nextTierMap = new HashMap<>();
    public static HashMap<Arena, Long> timeToNextUpdate = new HashMap<>();
    private final HashMap<Arena, BukkitTask> tasksToKill = new HashMap<>();
    private BukkitTask placeHolderTask = null;

    @EventHandler
    public void onGameStart(RoundStartEvent e) {
        final Arena arena = e.getArena();
        final boolean enabled = plugin().getConfig().getBoolean("Gen-Tiers-Enabled");

        if (enabled) {
            //Start updating placeholders
            if (placeHolderTask == null) {
                placeHolderTask = startUpdatingTime();
            }

            final List<String> tierOneSpawners = ServerManager.getConfig().getStringList("Tier-One-Titles.Spawners");
            final String tierLevel = ServerManager.getConfig().getString("Tier-One-Titles.Tier-Name");
            if (ServerManager.getConfig().getBoolean("Gen-Tiers-Holos-Enabled")) {

                //Add custom Holo titles
                for (Spawner spawner : arena.getSpawners()) {
                    final String itemType = getItemType(spawner);
                    for (String type : tierOneSpawners) {
                        if (type.equalsIgnoreCase(itemType)) {
                            spawner.setOverridingHologramLines(formatHoloTiles(tierLevel, spawner).toArray(new String[0]));
                        }
                    }
                }
            }
            scheduleTier(arena, 0);
        }
    }

    @EventHandler
    public void onGameStop(RoundEndEvent event) {

        //Kill Gen Tiers on round end
        BukkitTask task = tasksToKill.get(event.getArena());
        if (task != null) {
            task.cancel();
            tasksToKill.remove(event.getArena());
        }

        //Dont kill task if
        for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
            if (arena.getStatus() == ArenaStatus.RUNNING) {
                return;
            }
        }

        //Kill task
        if (placeHolderTask != null) {
            placeHolderTask.cancel();
            placeHolderTask = null;
        }
    }

    private void scheduleTier(Arena arena, int key) {

        //Array of all tier config section names
        String[] orderedList = section.getKeys(false).toArray(new String[0]);

        //Check if gone through all tiers
        if (orderedList.length < key) {
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
        if (task != null)
            task.cancel();

        switch (group.toLowerCase()) {
            case "game-over":
                arena.setIngameTimeRemaining((int) (time * 60));
                break;
            case "bed-break":
                tasksToKill.put(arena, Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
                    if (arena.getStatus() == ArenaStatus.RUNNING) {
                        //Break beds, start next tier
                        scheduleTier(arena, newKey);
                        ScheduleBedBreak.breakArenaBeds(arena);
                    }
                }, time * 20 * 60));
                break;
            default:
                tasksToKill.put(arena, Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
                    if (arena.getStatus() == ArenaStatus.RUNNING) {
                        scheduleTier(arena, newKey);
                        arena.broadcast(Message.build(chat));

                        //For all spawners
                        for (Spawner s : arena.getSpawners()) {
                            if (getItemType(s).equalsIgnoreCase(spawnerType)) {
                                //Set drop time
                                s.addDropDurationModifier("GEN_TIER_UPDATE", MBedwarsTweaksPlugin.getInstance(), SpawnerDurationModifier.Operation.SET, speed);
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

    private static BukkitTask startUpdatingTime() {
        return Bukkit.getServer().getScheduler().runTaskTimer(MBedwarsTweaksPlugin.getInstance(), () -> {
            if (timeToNextUpdate.isEmpty())
                return;

            timeToNextUpdate.forEach((arena, integer) -> {
                if (arena.getStatus() != ArenaStatus.RUNNING)
                    return;

                if (MBedwarsTweaksPlugin.papiEnabled)
                    timeToNextUpdate.replace(arena, integer, integer - 20);

                if (scoreBoardUpdating && ((integer - 20) / 20) % scoreBoardUpdatingInterval == 0)
                    arena.updateScoreboard();

            });
        }, 0L, 20L);
    }

    //Get spawner dropping material
    private String getItemType(Spawner s) {
        for (ItemStack i : s.getDropType().getDroppingMaterials()) {
            return i.getType().name();
        }
        return "";
    }

    //Format time for placeholder
    public static String[] timeLeft(Arena arena) {

        final int timeoutTicks = Math.toIntExact(timeToNextUpdate.get(arena));
        final int timeoutSeconds = (timeoutTicks / 20);

        final int minutes = (timeoutSeconds / 60) % 60;
        final int seconds = timeoutSeconds % 60;

        if (minutes + seconds > 0) {
            if (seconds < 10) {
                return new String[]{String.valueOf(minutes), "0" + seconds};
            } else {
                return new String[]{String.valueOf(minutes), String.valueOf(seconds)};
            }
        } else if (seconds == 0 && minutes > 0) {
            return new String[]{String.valueOf(minutes), "00"};
        } else {
            return new String[]{"0", "00"};
        }
    }

    //Format custom holo titles
    private List<String> formatHoloTiles(String tier, Spawner spawner) {
        final String spawnerName = spawner.getDropType().getConfigName();
        final String colorCode = "&" + spawnerName.charAt(1);
        final String strippedSpawnerName = spawnerName.substring(2);
        final List<String> formatted = new ArrayList<>();

        for (String string : spawnerTitleLines) {
            final String formattedString = Message.build(string)
                    .placeholder("{tier}", tier)
                    .placeholder("{spawner-color}", colorCode)
                    .placeholder("{spawner}", strippedSpawnerName)
                    .done();
            formatted.add(formattedString);
        }

        return formatted;
    }
}