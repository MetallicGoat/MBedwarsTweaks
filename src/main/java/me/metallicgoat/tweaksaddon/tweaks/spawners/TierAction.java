package me.metallicgoat.tweaksaddon.tweaks.spawners;

import lombok.Getter;

@Getter
public enum TierAction {
  GEN_UPGRADE,
  BED_DESTROY,
  GAME_OVER;

  private final String id;

  TierAction(){
    this.id = TierAction.this.name().toLowerCase().replace("_", "-");
  }
}
