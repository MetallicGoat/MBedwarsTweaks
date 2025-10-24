package me.metallicgoat.tweaksaddon.impl.api;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import java.util.Collection;
import java.util.List;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierHandler;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierState;
import me.metallicgoat.tweaksaddon.api.gentiers.SuddenDeathDragon;
import me.metallicgoat.tweaksaddon.api.unsafe.MBedwarsTweaksAPILayer;
import me.metallicgoat.tweaksaddon.config.GenTiersConfig;
import me.metallicgoat.tweaksaddon.gentiers.GenTiers;
import me.metallicgoat.tweaksaddon.gentiers.dragons.DragonUtil;
import me.metallicgoat.tweaksaddon.utils.Util;

public class ImplMBedwarsTweaksAPILayer extends MBedwarsTweaksAPILayer {
  @Override
  public List<SuddenDeathDragon> getDragons(Arena arena, Team team) {
    return DragonUtil.getDragons(arena, team);
  }

  @Override
  public GenTierState getGenTierStates(Arena arena) {
    return GenTiers.getState(arena);
  }

  @Override
  public void registerGenTierHandler(GenTierHandler handler) {
    Util.registerGenTierHandler(handler);
  }

  @Override
  public void unregisterGenTierHandler(String id) {
    Util.unregisterGenTierHandler(id);
  }

  @Override
  public GenTierHandler getGenTierHandlerById(String id) {
    return Util.getGenTierHandlerById(id);
  }

  @Override
  public Collection<GenTierHandler> getGenTierHandlers() {
    return Util.getGenTierHandlers();
  }

  @Override
  public Collection<GenTierLevel> getGenTierLevels() {
    return GenTiersConfig.gen_tier_levels.values();
  }

  @Override
  public GenTierLevel getGenTierLevel(int lvl) {
    return GenTiersConfig.gen_tier_levels.get(lvl);
  }

  @Override
  public void reloadGenTierConfig() {
    GenTiersConfig.load();
  }
}
