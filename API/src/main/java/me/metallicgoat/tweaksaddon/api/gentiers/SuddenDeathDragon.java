package me.metallicgoat.tweaksaddon.api.gentiers;


import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;

public interface SuddenDeathDragon {

  /**
   * The Bukkit entity used to power this dragon
   *
   * @return a Bukkit EnderDragon
   */
  EnderDragon getDragon();

  /**
   * The arena which this Dragon is active in
   *
   * @return the Arena the Dragon is playing in
   */
  Arena getArena();

  /**
   * The Dragon's Team
   *
   * @return The team the dragon is playing for
   */
  Team getTeam();

  /**
   * The location the dragon is currently targeting
   *
   * @return the dragons taget location
   */
  Location getDragonTargetLocation();

  /**
   * Change the location the Dragon is targeting
   *
   * @param location where the dragon should target
   */
  void setDragonTarget(Location location);

  /**
   * Change the entity which the Dragon is targeting
   *
   * @param entity who the dragon should target
   */
  void setDragonTarget(Entity entity);

  /**
   * Removes this dragon anf ends the corresponding tasks
   */
  void remove();

}
