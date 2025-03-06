package me.metallicgoat.tweaksaddon.utils;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.arena.picker.ArenaPickerAPI;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.location.XYZYP;
import java.lang.reflect.Method;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Util {

  public static void removePlayerItem(HumanEntity player, ItemStack item) {
    item = item == null ? new ItemStack(Material.AIR) : item;

    // Try using papers method
    if (NMSHelper.get().isRunningPaper() && NMSHelper.get().getVersion() > 8) {
      try {
        final Method setItemMethod = PlayerInventory.class.getDeclaredMethod("removeItemAnySlot", ItemStack.class);

        setItemMethod.invoke(player, item);

        return;

      } catch (Exception ignored) { }
    }

    // regular way
    player.getInventory().removeItem(item);

    // Check offhand too
    final ItemStack offHand = getItemInOffHand(player);

    if (offHand != null && offHand.isSimilar(item)) {
      setItemInOffHand(player, new ItemStack(Material.AIR));
    }
  }

  public static @Nullable ItemStack getItemInOffHand(HumanEntity player) {
    if (NMSHelper.get().getVersion() == 8)
      return null;

    try {
      return (ItemStack) PlayerInventory.class.getDeclaredMethod("getItemInOffHand", null).invoke(player.getInventory());
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public static void setItemInOffHand(HumanEntity player, @Nullable ItemStack itemInOffHand) {
    if (NMSHelper.get().getVersion() == 8)
      return;

    itemInOffHand = itemInOffHand == null ? new ItemStack(Material.AIR) : itemInOffHand;

    try {
      final Method setItemMethod = PlayerInventory.class.getDeclaredMethod("setItemInOffHand", ItemStack.class);

      setItemMethod.invoke(player, itemInOffHand);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Collection<Arena> parseArenas(String arenaKey) {
    if (arenaKey.equalsIgnoreCase("ALL-ARENAS")) {
      return GameAPI.get().getArenas();
    }

    final Arena arenaByName = GameAPI.get().getArenaByName(arenaKey);

    if (arenaByName != null)
      return Collections.singleton(arenaByName);

    try {
      return ArenaPickerAPI.get().getArenasByCondition(arenaKey);
    } catch (Exception ignored) {
      return Collections.emptyList();
    }
  }

  public static @Nullable DropType getDropType(String id) {
    return GameAPI.get().getDropTypeById(id);
  }

  public static boolean isInteger(String s) {
    try {
      Integer.valueOf(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static List<Location> getAllTeamSpawns(Arena arena, World world, Team ignoreTeam) {
    final List<Location> targets = new ArrayList<>();

    for (Team currTeam : arena.getEnabledTeams()) {
      if (currTeam == ignoreTeam)
        continue;

      final XYZYP spawn = arena.getTeamSpawn(currTeam);

      if (spawn != null)
        targets.add(spawn.toLocation(world));
    }

    return targets;
  }

  public static List<String> getLines(String string) {
    final String[] parts = string.split("\\\\n");
    return Arrays.asList(parts);
  }
}
