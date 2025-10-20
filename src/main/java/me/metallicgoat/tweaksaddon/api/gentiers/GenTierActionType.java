package me.metallicgoat.tweaksaddon.api.gentiers;

import lombok.Getter;
import me.metallicgoat.tweaksaddon.api.GenTiersAPI;
import me.metallicgoat.tweaksaddon.gentiers.handlers.*;

@Getter
public enum GenTierActionType {

  /**
   * Affects drop rates of in game spawners
   */
  GEN_UPGRADE(new SpawnerUpgradeHandler()),

  /**
   * Destroys all remaining beds within an arena
   */
  BED_DESTROY(new BedDestroyHandler()),

  /**
   * Spawns in dragons to aid in ending the game faster
   */
  SUDDEN_DEATH(new SuddenDeathHandler()),

  /**
   * The tier that officially ends the game
   */
  GAME_OVER(new GameOverHandler()),

  /**
   * Custom Gen Tier Action
   */
  PLUGIN(null);

  private static boolean init = false;

  private final String defaultHandlerId;
  private final GenTierHandler handler;

  GenTierActionType(GenTierHandler handler){
    this.defaultHandlerId = GenTierActionType.this.name().toLowerCase().replace("_", "-");
    this.handler = handler;
  }

  /**
   * @deprecated Not a part of the public API
   */
  @Deprecated
  public static void initDefaults() {
    if (init) return;

    for (GenTierActionType type : GenTierActionType.values()) {
      if (type == PLUGIN) continue;

      GenTiersAPI.registerHandler(type.handler);
    }

    init = true;
  }
}
