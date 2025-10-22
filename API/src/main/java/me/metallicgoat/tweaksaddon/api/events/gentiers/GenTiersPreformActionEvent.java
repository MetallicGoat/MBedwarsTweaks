package me.metallicgoat.tweaksaddon.api.events.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.ArenaEvent;
import lombok.Getter;
import lombok.Setter;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierLevel;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GenTiersPreformActionEvent extends Event implements ArenaEvent {

  private static final HandlerList HANDLERS = new HandlerList();

  @Getter
  private final Arena arena;
  @Getter
  private final GenTierLevel level;
  @Getter @Setter
  private boolean executingHandlers;
  @Getter @Setter
  private boolean broadcastingEarn;

  public GenTiersPreformActionEvent(Arena arena, GenTierLevel level, boolean executeHandlers, boolean broadcastEarn) {
    this.arena = arena;
    this.level = level;
    this.executingHandlers = executeHandlers;
    this.broadcastingEarn = broadcastEarn;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}