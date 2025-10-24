package me.metallicgoat.tweaksaddon.api.events.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.ArenaEvent;
import lombok.Getter;
import lombok.Setter;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when enough time has passed to execute the scheduled gen tier upgrade in an arena.
 * <p>
 *   This event is not called for {@link me.metallicgoat.tweaksaddon.api.gentiers.GenTierActionType#GAME_OVER},
 *   as that one immediately changes the remaining round time after its {@link GenTiersScheduleEvent}.
 * </p>
 */
public class GenTiersActionEvent extends Event implements ArenaEvent {

  private static final HandlerList HANDLERS = new HandlerList();

  private final GenTierState arenaState;
  private final GenTierLevel tier;

  @Getter @Setter
  private boolean executingHandlers;
  @Getter @Setter
  private boolean broadcastingEarn;

  public GenTiersActionEvent(GenTierState arenaState, GenTierLevel tier, boolean executeHandlers, boolean broadcastEarn) {
    this.arenaState = arenaState;
    this.tier = tier;
    this.executingHandlers = executeHandlers;
    this.broadcastingEarn = broadcastEarn;
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
   * Get the tier level that is being applied.
   *
   * @return the tier level being applied
   */
  public GenTierLevel getTier() {
    return this.tier;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}