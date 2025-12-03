package me.metallicgoat.tweaksaddon.api.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import java.time.Duration;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the current state of the gen tier system in an arena.
 */
public interface GenTierState {

  /**
   * Get the arena this GenTierState belongs to.
   *
   * @return The arena this GenTierState belongs to.
   */
  Arena getArena();

  /**
   * Check if the gen tier system is currently active in this arena.
   *
   * @return <code>true</code> if the gen tier system is active, <code>false</code> otherwise.
   */
  boolean isValid();

  /**
   * Check whether a team has bought a team has bought the sudden-death upgrade.
   * <p>
   *   As a result, a dragon will be spawned for the team on sudden death.
   * </p>
   *
   * @param team The team to check.
   * @return <code>true</code> if the team has bought the sudden-death upgrade, <code>false</code> otherwise.
   */
  boolean hasBoughtDragon(Team team);

  /**
   * Set whether a team has bought the sudden-death upgrade.
   * <p>
   *   As a result, a dragon will be spawned for the team on sudden death.
   * </p>
   *
   * @param team The team to set.
   * @param state The state to set.
   */
  void setDragonBought(Team team, boolean state);

  /**
   * Get the remaining time until the next gen tier level is reached.
   * <p>
   *   Doesn't go below {@link Duration#ZERO}. Might stay at zero for a very short
   *   time (max a few seconds) if the server is lagging and the scheduler didn't catch up yet.
   * </p>
   *
   * @return The remaining time until the next gen tier level is reached, or <code>null</code> if there is no next gen tier level.
   */
  @Nullable
  Duration getRemainingNextTier();

  /**
   * Set the remaining time until the next gen tier level is reached.
   *
   * @param duration The duration to set
   * @throws IllegalStateException if there is no next gen tier level
   */
  void setRemainingNextTier(Duration duration);

  /**
   * Get the tier level that already has been reached.
   *
   * @return the current gen tier level, or <code>null</code> if no gen tier level has been reached yet
   */
  @Nullable
  GenTierLevel getCurrentTier();

  /**
   * Get the next gen tier level that will be reached.
   *
   * @return the next gen tier level, or <code>null</code> if there is no next gen tier level
   */
  @Nullable
  GenTierLevel getNextTier();

  /**
   * Schedule the next gen tier level to be reached.
   *
   * @param level the next gen tier level to be reached
   */
  void scheduleNextTier(GenTierLevel level);

  /**
   * Schedule the next gen tier level to be reached.
   *
   * @param level the next gen tier level to be reached
   * @param time the duration until the next gen tier level is reached
   */
  void scheduleNextTier(GenTierLevel level, Duration time);

  /**
   * Cancel the current gen tier progression and stops any further.
   */
  void cancelTiers();
}