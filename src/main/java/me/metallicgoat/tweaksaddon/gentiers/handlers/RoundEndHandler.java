package me.metallicgoat.tweaksaddon.gentiers.handlers;

import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.gentiers.GenTierLevel;

public class RoundEndHandler extends GenTierHandler {
  @Override
  public void run(GenTierLevel level, Arena arena) {
    arena.setIngameTimeRemaining((int) (level.getTime() * 60));
  }
}
