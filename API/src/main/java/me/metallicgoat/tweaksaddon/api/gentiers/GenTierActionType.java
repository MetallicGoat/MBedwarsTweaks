package me.metallicgoat.tweaksaddon.api.gentiers;

public enum GenTierActionType {

  /**
   * Affects drop rates of in game spawners
   */
  GEN_UPGRADE,

  /**
   * Destroys all remaining beds within an arena
   */
  BED_DESTROY,

  /**
   * Spawns in dragons to aid in ending the game faster
   */
  SUDDEN_DEATH,

  /**
   * The tier that officially ends the game
   */
  GAME_OVER,

  /**
   * Custom Gen Tier Action
   */
  PLUGIN;

  private final String defaultHandlerId;

  GenTierActionType() {
    this.defaultHandlerId = GenTierActionType.this.name().toLowerCase().replace("_", "-");
  }

  public String getDefaultHandlerId() {
    if (this == GenTierActionType.PLUGIN)
      throw new UnsupportedOperationException("PLUGIN does not have a default handler Id!");

    return defaultHandlerId;
  }
}
