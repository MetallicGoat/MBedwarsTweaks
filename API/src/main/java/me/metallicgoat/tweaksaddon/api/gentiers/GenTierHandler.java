package me.metallicgoat.tweaksaddon.api.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.api.unsafe.MBedwarsTweaksAPILayer;
import org.bukkit.plugin.Plugin;

/**
 * Represents a handler for a gen tier occurance.
 *
 * @see me.metallicgoat.tweaksaddon.api.GenTiersAPI#registerHandler(GenTierHandler)
 */
public abstract class GenTierHandler {

  /**
   * Called when the gen tier's time elapsed and it is being run.
   *
   * @param level the gen tier level being run
   * @param arena the arena the gen tier is being run in
   */
  public abstract void run(GenTierLevel level, Arena arena);

  /**
   * Get the plugin which registered this handler
   *
   * @return the plugin which registered this handler
   */
  public abstract Plugin getPlugin();

  /**
   * Get the unique id of this handler as used in configs
   *
   * @return the unique id of this handler
   */
  public abstract String getId();

  /**
   * Get the type of action this handler performs
   * <p>
   *   Custom handlers should always return {@link GenTierActionType#PLUGIN}
   * </p>
   *
   * @return the type of action this handler performs
   */
  public GenTierActionType getActionType() {
    return GenTierActionType.PLUGIN;
  }

  /**
   * Check if this handler is currently registered
   *
   * @return true if this handler is registered
   */
  public final boolean isRegistered() {
    return MBedwarsTweaksAPILayer.INSTANCE.getGenTierHandlers().contains(this);
  }
}
