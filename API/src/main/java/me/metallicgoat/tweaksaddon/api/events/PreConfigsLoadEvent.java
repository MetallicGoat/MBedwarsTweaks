package me.metallicgoat.tweaksaddon.api.events;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called directly before the plugin configuration files have are being loaded or reloaded.
 */
public class PreConfigsLoadEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  private final boolean firstLoad;

  public PreConfigsLoadEvent(boolean firstLoad) {
    this.firstLoad = firstLoad;
  }

  /**
   * Whether this event represents the first time the configuration has been loaded.
   *
   * @return {@code true} if it was the first time the config was loaded (Plugin just enabled)
   */
  public boolean isFirstLoad() {
    return this.firstLoad;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}
