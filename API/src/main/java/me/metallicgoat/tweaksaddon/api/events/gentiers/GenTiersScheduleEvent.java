package me.metallicgoat.tweaksaddon.api.events.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.ArenaEvent;
import de.marcely.bedwars.tools.Validate;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a gen tier upgrade is scheduled to occur in an arena.
 * <p>
 *   This occurs either when the arena starts or when a previous tier upgrade has completed.
 * </p>
 */
public class GenTiersScheduleEvent extends Event implements ArenaEvent, Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();

  private final GenTierState arenaState;

  private GenTierLevel nextTier;
  private Duration delay;

  @Getter @Setter
  private boolean cancelled = false;

  public GenTiersScheduleEvent(GenTierState arenaState, GenTierLevel nextTier, Duration delay) {
    this.arenaState = arenaState;
    this.nextTier = nextTier;
    this.delay = delay;
  }

  @Override
  public Arena getArena() {
    return this.arenaState.getArena();
  }

  /**
   * Get the state of the gen tiers in the arena.
   *
   * @return the current gen tier state of the arena
   */
  public GenTierState getArenaState() {
    return this.arenaState;
  }

  /**
   * Get the next tier level that the arena will upgrade to.
   *
   * @return the next tier level to be applied after the delay
   */
  public GenTierLevel getNextTier() {
    return this.nextTier;
  }

  /**
   * Sets the next tier level that the arena will upgrade to.
   *
   * @param nextTier the next tier level to set after the delay
   */
  public void setNextTier(GenTierLevel nextTier) {
    Validate.notNull(nextTier, "nextTier");

    this.nextTier = nextTier;
  }

  /**
   * Get the delay before the initial tier happens.
   *
   * @return the delay before the first tier upgrade
   */
  public Duration getDelay() {
    return this.delay;
  }

  /**
   * Sets the delay before the initial tier happens.
   *
   * @param delay the delay to set
   */
  public void setDelay(Duration delay) {
    Validate.notNull(delay, "delay");

    this.delay = delay;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}
