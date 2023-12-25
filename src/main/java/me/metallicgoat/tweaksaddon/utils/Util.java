package me.metallicgoat.tweaksaddon.utils;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.picker.ArenaPickerAPI;
import de.marcely.bedwars.api.game.spawner.DropType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class Util {

  // TODO make better
  public static String[] formatMinSec(int minutes, int seconds) {
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

  public static List<String> getLines(String string) {
    final String[] parts = string.split("\\\\n");
    return Arrays.asList(parts);
  }
}
