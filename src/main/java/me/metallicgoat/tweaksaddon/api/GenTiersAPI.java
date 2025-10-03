package me.metallicgoat.tweaksaddon.api;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.tools.Validate;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierHandler;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierState;
import me.metallicgoat.tweaksaddon.api.gentiers.SuddenDeathDragon;
import me.metallicgoat.tweaksaddon.gentiers.GenTiers;
import me.metallicgoat.tweaksaddon.gentiers.dragons.DragonUtil;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class GenTiersAPI {

  /**
   * Get the all sudden death dragons, or for a particular arena/team which are actively alive
   *
   * @param arena The arena which we are truing to get the dragons
   * @param team The team which the dragon belongs to
   * @return A list of actively running SuddenDeathDragons
   */
  public static List<SuddenDeathDragon> getDragons(Arena arena, Team team) {
    return DragonUtil.getDragons(arena, team);
  }

  /**
   * Get the GenTierState of an arena
   * Will return <code>null</code> if the gen tier system is not running in the specified arena
   *
   * @param arena the arena where we are grabbing the GenTierState from
   * @return The GenTierState of the corresponding arena
   */
  public static GenTierState getGenTierStates(Arena arena) {
    return GenTiers.getState(arena);
  }

  /**
   * Register a custom GenTierHandler! You will need to implement the GenTierHandler
   *
   * @param handler The GenTier you are registering
   */
  public static void registerGenTierHandler(GenTierHandler handler) {
    Validate.notNull(handler, "handler");

    Util.registerGenTierHandler(handler);
  }

  /**
   * Unregisters a Gen Tier Handler. Using this you could replace internal handlers
   *
   * @param id The id of the handler
   */
  public static void unregisterGenTierHandler(String id) {
    Validate.notNull(id, "Id cannot be null");

    Util.unregisterGenTierHandler(id);
  }

  /**
   * Gets a GenTierHandler by its id
   *
   * @param id The id of the handler
   */
  public static @Nullable GenTierHandler getGenTierHandlersById(String id) {
    Validate.notNull(id, "Id cannot be null");

    return Util.getGenTierHandlerById(id);
  }

  /**
   * Get all registered GenTierHandlers
   *
   * @return all registered GenTierHandlers
   */
  public static Collection<GenTierHandler> getGenTierHandlers() {
    return Util.getGenTierHandlers();
  }
}
