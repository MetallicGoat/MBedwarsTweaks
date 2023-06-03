package me.metallicgoat.tweaksaddon.schedular;

import de.marcely.bedwars.api.arena.Arena;

public abstract class ArenaSchedulerHandler {

  public final ArenaScheduler scheduler;

  public ArenaSchedulerHandler(ArenaScheduler scheduler){
    this.scheduler = scheduler;
  }

  /**
   * The length of time before each update
   *
   * @return how many ticks between each call of execute
   */
  public abstract long getUpdateInterval();

  public abstract void execute();


  public void roundStart(Arena arena){ }

  public void roundEnd(Arena arena) { }
}
