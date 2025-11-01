package me.metallicgoat.tweaksaddon.api;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import de.marcely.bedwars.tools.Validate;
import java.util.Collections;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierHandler;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierState;
import me.metallicgoat.tweaksaddon.api.gentiers.SuddenDeathDragon;
import me.metallicgoat.tweaksaddon.api.unsafe.MBedwarsTweaksAPILayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Represents the API to interact with the Gen Tiers system of MBedwars Tweaks
 */
public class GenTiersAPI {

  /**
   * Get the dragons for a specific team in an arena.
   *
   * @param arena The arena which we are truing to get the dragons
   * @param team The team which the dragon belongs to
   * @return A list of actively running SuddenDeathDragons
   */
  public static List<SuddenDeathDragon> getDragons(Arena arena, Team team) {
    Validate.notNull(arena, "arena");
    Validate.notNull(team, "team");

    return MBedwarsTweaksAPILayer.INSTANCE.getDragons(arena, team);
  }

  /**
   * Get the dragons for arena.
   *
   * @param arena The arena which we are truing to get the dragons
   * @return A list of actively running SuddenDeathDragons
   */
  public static List<SuddenDeathDragon> getDragons(Arena arena) {
    Validate.notNull(arena, "arena");

    return MBedwarsTweaksAPILayer.INSTANCE.getDragons(arena, null);
  }

  /**
   * Get all existing dragons.
   *
   * @return A list of actively running SuddenDeathDragons
   */
  public static List<SuddenDeathDragon> getDragons() {
    return MBedwarsTweaksAPILayer.INSTANCE.getDragons(null, null);
  }

  /**
   * Get the GenTierState of an arena
   * Will return <code>null</code> if the gen tier system is not running in the specified arena
   *
   * @param arena the arena where we are grabbing the GenTierState from
   * @return The GenTierState of the corresponding arena
   */
  @Nullable
  public static GenTierState getState(Arena arena) {
    Validate.notNull(arena, "arena");

    return MBedwarsTweaksAPILayer.INSTANCE.getGenTierState(arena);
  }

  /**
   * Get all configured Gen Tiers
   *
   * @return An unmodifiable collection of all configured Gen Tiers
   */
  public static Collection<GenTierLevel> getTiers() {
    return Collections.unmodifiableCollection(MBedwarsTweaksAPILayer.INSTANCE.getGenTierLevels());
  }

  /**
   * Get a specific Gen Tier by its level
   *
   * @param level The level of the Gen Tier you are trying to get
   * @return The Gen Tier of the corresponding level
   */
  @Nullable
  public static GenTierLevel getTier(int level) {
    return MBedwarsTweaksAPILayer.INSTANCE.getGenTierLevel(level);
  }

  /**
   * Get the first Gen Tier (level 1)
   *
   * @return The Gen Tier of level 1
   */
  @Nullable
  public static GenTierLevel getFirstTier() {
    return MBedwarsTweaksAPILayer.INSTANCE.getGenTierLevel(1);
  }

  /**
   * Register a custom GenTierHandler! You will need to implement the GenTierHandler
   *
   * @param handler The GenTier you are registering
   */
  public static void registerHandler(GenTierHandler handler) {
    Validate.notNull(handler, "handler");

    MBedwarsTweaksAPILayer.INSTANCE.registerGenTierHandler(handler);
  }

  /**
   * Unregisters a Gen Tier Handler. Using this you could replace internal handlers
   *
   * @param id The id of the handler
   */
  public static void unregisterHandler(String id) {
    Validate.notNull(id, "id");

    MBedwarsTweaksAPILayer.INSTANCE.unregisterGenTierHandler(id);
  }

  /**
   * Gets a GenTierHandler by its id
   *
   * @param id The id of the handler
   */
  @Nullable
  public static GenTierHandler getHandlerById(String id) {
    Validate.notNull(id, "id");

    return MBedwarsTweaksAPILayer.INSTANCE.getGenTierHandlerById(id);
  }

  /**
   * Get all registered GenTierHandlers
   *
   * @return all registered GenTierHandlers
   */
  public static Collection<GenTierHandler> getHandlers() {
    return MBedwarsTweaksAPILayer.INSTANCE.getGenTierHandlers();
  }

  /**
   * Get the trigger handler used for the sudden death team upgrade.
   *
   * @return The singletone UpgradeTriggerHandler instance for the sudden death upgrade
   */
  public UpgradeTriggerHandler getSuddenDeathUpgrade() {
    return MBedwarsTweaksAPILayer.INSTANCE.getSuddenDeathUpgrade();
  }

  /**
   * Reloads the gen-tiers config and applies any changes made to it
   * <p>
   *   Note: This will NOT update any arenas that are currently running the gen tier system,
   *   and as a result might cause weird states for active arenas.
   *   Runs IO on the same thread that it was called on.
   * </p>
   */
  public static void reloadConfig() {
    MBedwarsTweaksAPILayer.INSTANCE.reloadGenTierConfig();
  }
}