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

  private static Method GET_ITEM_IN_OFF_HAND_METHOD = null;

  public static void init() {
    if (NMSHelper.get().getVersion() == 8)
      return;

    try {
      GET_ITEM_IN_OFF_HAND_METHOD = PlayerInventory.class.getDeclaredMethod("getItemInOffHand", null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static @Nullable ItemStack getItemInOffHand(HumanEntity player) {
    if (GET_ITEM_IN_OFF_HAND_METHOD == null)
      return null;

    try {
      return (ItemStack) GET_ITEM_IN_OFF_HAND_METHOD.invoke(player.getInventory());
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
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
