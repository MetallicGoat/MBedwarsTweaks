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

public class SuddenDeathDragonTargetEvent extends Event implements ArenaEvent, Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();

  @Getter
  private final Arena arena;
  @Getter
  private final SuddenDeathDragon dragon;
  @Getter
  private Entity targetEntity;
  @Getter
  private final Entity previousTargetEntity;
  @Getter
  private final boolean previouslyTargetingEntity;
  @Getter
  private boolean targetingEntity;
  @Getter
  private Location targetLocation;
  @Getter
  private final Location previousTargetLocation;
  @Getter @Setter
  private boolean cancelled = false;

  public SuddenDeathDragonTargetEvent(
      Arena arena,
      SuddenDeathDragon dragon,
      @Nullable Entity targetEntity,
      Location targetLocation,
      Location previousTargetLocation,
      Entity previousTargetEntity,
      boolean previouslyTargetingEntity
  ) {
    this.arena = arena;
    this.dragon = dragon;
    this.targetEntity = targetEntity;
    this.targetLocation = targetLocation;
    this.previousTargetLocation = previousTargetLocation;
    this.previousTargetEntity = previousTargetEntity;
    this.previouslyTargetingEntity = previouslyTargetingEntity;
  }

  /**
   * Change the entity which the dragon is targeting
   * NOTE: The entity must be in the same world as the dragon
   *
   * @param targetEntity to target
   */
  public void setTargetEntity(Entity targetEntity) {
    Validate.isTrue(targetEntity == null || (targetEntity.isValid() && targetEntity.getWorld() == this.dragon.getDragon().getWorld()), "Target entity must be in the same world as the dragon");

    this.targetEntity = targetEntity;
    this.targetLocation = targetEntity.getLocation();
  }

  /**
   * Change the location which the dragon is targeting
   * NOTE: The location must be in the same world as the dragon
   *
   * @param targetLocation to target
   */
  public void setTargetLocation(Location targetLocation) {
    Validate.isTrue(targetLocation == null || targetLocation.getWorld() == this.dragon.getDragon().getWorld(), "Target location must be as the dragon's world");

    this.targetLocation = targetLocation;
  }


  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}