package me.metallicgoat.tweaksaddon.gentiers.handlers;

import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.gentiers.GenTierLevel;

public abstract class GenTierHandler {
  public abstract void run(GenTierLevel level, Arena arena);
}
