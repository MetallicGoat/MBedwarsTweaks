package me.metallicgoat.tweaksaddon.api.gentiers;

import de.marcely.bedwars.api.arena.Team;

public interface GenTierState {

  GenTierLevel getNextGenTierLevel();

  GenTierLevel getCurrentGenTierLevel();

  boolean hasDragon(Team team);

  void addDragonTeam(Team team);

  int getSecondsToNextTier();
}