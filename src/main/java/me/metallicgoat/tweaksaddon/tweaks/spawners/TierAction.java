package me.metallicgoat.tweaksaddon.tweaks.spawners;

public enum TierAction {
  GEN_UPGRADE,
  BED_DESTROY,
  GAME_OVER;

  public String getId() {
    return TierAction.BED_DESTROY.name().toLowerCase().replace("_", "-");
  }
}
