package me.metallicgoat.tweaksaddon.api.gentiers;

import de.marcely.bedwars.api.arena.Arena;

public abstract class GenTierHandler {
  public abstract void run(GenTierLevel level, Arena arena);

  public abstract String getId();

  public GenTierActionType getActionType() {
    return GenTierActionType.PLUGIN;
  }
}
