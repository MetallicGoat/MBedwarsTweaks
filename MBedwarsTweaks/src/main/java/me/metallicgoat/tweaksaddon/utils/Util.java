package me.metallicgoat.tweaksaddon.utils;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.arena.picker.ArenaPickerAPI;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.location.XYZYP;
import java.lang.reflect.Method;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierActionType;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierHandler;
import me.metallicgoat.tweaksaddon.api.unsafe.MBedwarsTweaksAPILayer;
import me.metallicgoat.tweaksaddon.gentiers.handlers.BedDestroyHandler;
import me.metallicgoat.tweaksaddon.gentiers.handlers.GameOverHandler;
import me.metallicgoat.tweaksaddon.gentiers.handlers.SpawnerUpgradeHandler;
import me.metallicgoat.tweaksaddon.gentiers.handlers.SuddenDeathHandler;
import me.metallicgoat.tweaksaddon.impl.api.ImplMBedwarsTweaksAPILayer;
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
import java.util.HashMap;
import java.util.List;

public class Util {

  private static final HashMap<String, GenTierHandler> handlersById = new HashMap<>();

  private static Method GET_ITEM_IN_OFF_HAND_METHOD = null;

  public static void init() {
    MBedwarsTweaksAPILayer.INSTANCE = new ImplMBedwarsTweaksAPILayer();

    registerDefaultGenTierHandlers();

    if (NMSHelper.get().getVersion() == 8)
      return;

    try {
      GET_ITEM_IN_OFF_HAND_METHOD = PlayerInventory.class.getDeclaredMethod("getItemInOffHand", null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void registerDefaultGenTierHandlers() {
    registerGenTierHandler(new BedDestroyHandler());
    registerGenTierHandler(new GameOverHandler());
    registerGenTierHandler(new SpawnerUpgradeHandler());
    registerGenTierHandler(new SuddenDeathHandler());
  }

  public static void registerGenTierHandler(GenTierHandler handler) {
    if (handlersById.containsKey(handler.getId())) {
      throw new IllegalStateException("GenTierHandler already registered");
    }

    handlersById.put(handler.getId().toLowerCase(), handler);
  }

  public static void unregisterGenTierHandler(String id) {
    handlersById.remove(id);
  }

  public static GenTierHandler getGenTierHandlerById(String id) {
    return handlersById.get(id);
  }

  public static Collection<GenTierHandler> getGenTierHandlers() {
    return handlersById.values();
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

  public static List<Material> buildMaterialList(String... materials) {
    final List<Material> materialList = new ArrayList<>();

    for (String material : materials) {
      final Material mat = Helper.get().getMaterialByName(material);

      if (mat != null)
        materialList.add(mat);
    }

    return materialList;
  }

  public static List<String> getLines(String string) {
    final String[] parts = string.split("\\\\n");
    return Arrays.asList(parts);
  }
}
