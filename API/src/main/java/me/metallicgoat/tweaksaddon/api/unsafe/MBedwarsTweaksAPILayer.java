package me.metallicgoat.tweaksaddon.api.unsafe;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierHandler;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierState;
import me.metallicgoat.tweaksaddon.api.gentiers.SuddenDeathDragon;
import java.util.Collection;
import java.util.List;


/**
 *
 * Do NOT use this class!
 * This is not API and is subject to change at any time
 *
 */

@Deprecated
public abstract class MBedwarsTweaksAPILayer {

  public static MBedwarsTweaksAPILayer INSTANCE;

  public abstract List<SuddenDeathDragon> getDragons(Arena arena, Team team);

  public abstract GenTierState getGenTierStates(Arena arena);

  public abstract void registerGenTierHandler(GenTierHandler handler);

  public abstract void unregisterGenTierHandler(String id);

  public abstract GenTierHandler getGenTierHandlerById(String id);

  public abstract Collection<GenTierHandler> getGenTierHandlers();

  public abstract Collection<GenTierLevel> getGenTierLevels();

  public abstract GenTierLevel getGenTierLevel(int lvl);

  public abstract void reloadGenTierConfig();

}
