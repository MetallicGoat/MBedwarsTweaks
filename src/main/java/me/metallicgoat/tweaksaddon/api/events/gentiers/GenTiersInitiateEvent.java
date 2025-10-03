package me.metallicgoat.tweaksaddon.api.events.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.ArenaEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GenTiersInitiateEvent extends Event implements ArenaEvent, Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();

  @Getter
  private final Arena arena;
  @Getter @Setter
  private int initialTierLevel;
  @Getter @Setter
  private boolean cancelled = false;

  public GenTiersInitiateEvent(Arena arena, int initialTierLevel) {
    this.arena = arena;
    this.initialTierLevel = initialTierLevel;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}
