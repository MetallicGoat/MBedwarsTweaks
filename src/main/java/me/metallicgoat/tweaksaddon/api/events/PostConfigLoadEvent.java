package me.metallicgoat.tweaksaddon.api.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PostConfigLoadEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  @Getter
  private final boolean firstLoad;

  public PostConfigLoadEvent(boolean firstLoad) {
    this.firstLoad = firstLoad;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}