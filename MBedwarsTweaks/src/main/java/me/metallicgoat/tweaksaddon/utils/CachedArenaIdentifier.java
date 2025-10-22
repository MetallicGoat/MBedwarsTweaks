package me.metallicgoat.tweaksaddon.utils;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.picker.ArenaPickerAPI;
import de.marcely.bedwars.api.arena.picker.condition.ArenaCondition;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * This class is used to cache the arena conditions.
 * It is used to avoid parsing the condition every time we need to check if an arena is included.
 * Handy for some configs + support for "ALL-ARENAS" from the old tweaks era
 */
public class CachedArenaIdentifier {

  @Getter
  private final String originalString;

  private boolean cached = false;

  private final boolean allArenas;
  private Arena arena;
  private ArenaCondition arenaCondition;

  public CachedArenaIdentifier(String originalString) {
    this.originalString = originalString;
    this.allArenas = originalString.equalsIgnoreCase("ALL-ARENAS");
  }

  public boolean includes(Arena arena) {
    cacheIfNeeded();

    return this.allArenas || (this.arena != null && this.arena == arena) || (this.arenaCondition != null && this.arenaCondition.check(arena));
  }

  public Collection<Arena> getArenas() {
    cacheIfNeeded();

    if (this.allArenas)
      return GameAPI.get().getArenas();

    if (this.arena != null)
      return Collections.singleton(this.arena);

    if (this.arenaCondition != null) {
      return GameAPI.get().getArenas().stream()
          .filter(arena -> this.arenaCondition.check(arena))
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  private void cacheIfNeeded() {
    if (this.cached || this.allArenas)
      return;

    this.cached = true;

    final Arena arenaByName = GameAPI.get().getArenaByName(this.originalString);

    if (arenaByName != null) {
      this.arena = arenaByName;

    } else {
      try {
        arenaCondition = ArenaPickerAPI.get().parseCondition(this.originalString);

      } catch (Exception ignored) {
      }
    }
  }
}
