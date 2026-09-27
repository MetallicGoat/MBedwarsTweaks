package me.metallicgoat.tweaksaddon.api.events.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.ArenaEvent;
import de.marcely.bedwars.tools.Validate;
import lombok.Getter;
import lombok.Setter;
import me.metallicgoat.tweaksaddon.api.gentiers.SuddenDeathDragon;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

/**
 * Gets called when a sudden death dragon needs to find a target to fly to.
 * <p>
 *   Either called right with the dragon's spawning, or once it reaches the prior target.
 * </p>
 */
public class SuddenDeathDragonTargetEvent extends Event implements ArenaEvent, Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();

  @Getter
  private final Arena arena;
  private final SuddenDeathDragon dragon;
  private Entity targetEntity;
  private final Entity previousTargetEntity;
  private Location targetLocation;
  private final Location previousTargetLocation;
  @Getter @Setter
  private boolean cancelled = false;

  public SuddenDeathDragonTargetEvent(
      Arena arena,
      SuddenDeathDragon dragon,
      @Nullable Entity targetEntity,
      Location targetLocation,
      Location previousTargetLocation,
      Entity previousTargetEntity
  ) {
    this.arena = arena;
    this.dragon = dragon;
    this.targetEntity = targetEntity;
    this.targetLocation = targetLocation;
    this.previousTargetLocation = previousTargetLocation;
    this.previousTargetEntity = previousTargetEntity;
  }

  /**
   * Get the dragon entity that is targeting something.
   *
   * @return The dragon involved in this event
   */
  public SuddenDeathDragon getDragon() {
    return this.dragon;
  }

  /**
   * Get the specifically targeted entity.
   *
   * @return The targeted entity. May be <code>null</code> if it's targeting a location instead
   */
  @Nullable
  public Entity getTargetEntity() {
    return this.targetEntity;
  }

  /**
   * Get whether the dragon is targeting an entity specifically.
   *
   * @return If an entity is specifically targeted
   */
  public boolean hasTargetEntity() {
    return this.targetEntity != null;
  }

  /**
   * Change the entity which the dragon is targeting.
   * <p>
   *  NOTE: The entity must be in the same world as the dragon.
   *  Passing <code>null</code> makes the dragon fly to {@link #getTargetLocation()} instead.
   * </p>
   * <p>
   *   Also changes the target-location to {@link Entity#getLocation()}, if it's non-null.
   * </p>
   *
   * @param targetEntity to target
   * @throws IllegalArgumentException If {@link Entity#isValid()} returns false
   * @throws IllegalArgumentException If the entity's world differs to the dragon's world
   */
  public void setTargetEntity(@Nullable Entity targetEntity) {
    if (targetEntity != null) {
      final Location loc = targetEntity.getLocation();

      Validate.isTrue(targetEntity.isValid(), "targetEntity#isValid() returns false (the targetEntity has been removed)");
      Validate.isTrue(loc.getWorld() == this.dragon.getDragon().getWorld(),
                      "Target entity must be in the same world as the dragon");

      this.targetLocation = loc;
      this.targetEntity = targetEntity;
    } else
      this.targetEntity = null;
  }

  /**
   * Get the (mutable) location the dragon shall fly to.
   *
   * @return The current target location
   */
  public Location getTargetLocation() {
    return this.targetLocation;
  }

  /**
   * Change the location which the dragon is targeting.
   * <p>
   *  NOTE: The location must be in the same world as the dragon.
   *  It also purges the current target-entity state, meaning
   *  {@link #hasTargetEntity()} will return <code>false</code> after this
   *  method is called.
   * </p>
   *
   * @param targetLocation to target
   * @throws IllegalArgumentException If the location's world differs to the dragon's world
   */
  public void setTargetLocation(Location targetLocation) {
    Validate.notNull(targetLocation, "targetLocation");
    Validate.isTrue(targetLocation.getWorld() == this.dragon.getDragon().getWorld(), "Target location must be as the dragon's world");

    this.targetLocation = targetLocation;
  }

  /**
   * Get the previous entity that has been targeted by this dragon.
   *
   * @return The entity target prior this event. May be <code>null</code> if there hasn't been any
   */
  @Nullable
  public Entity getPreviousTargetEntity() {
    return this.previousTargetEntity;
  }

  /**
   * Get the previous location that has been targeted by this dragon.
   *
   * @return The entity target prior this event. May be <code>null</code> if there hasn't been any
   */
  @Nullable
  public Location getPreviousTargetLocation() {
    return this.previousTargetLocation;
  }

  /**
   * Get whether an entity has ever been before targeted by this dragon.
   *
   * @return <code>true</code> if there has been one
   */
  public boolean hasPreviousTargetEntity() {
    return this.previousTargetEntity != null;
  }

  /**
   * Get whether a location has ever been before targeted by this dragon.
   *
   * @return <code>true</code> if there has been one
   */
  public boolean hasPreviousTargetLocation() {
    return this.previousTargetLocation != null;
  }


  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}