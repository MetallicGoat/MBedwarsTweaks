package me.metallicgoat.tweaksaddon.gentiers;

import lombok.Getter;
import me.metallicgoat.tweaksaddon.gentiers.handlers.GenTierHandler;
import me.metallicgoat.tweaksaddon.gentiers.handlers.RoundEndHandler;
import me.metallicgoat.tweaksaddon.gentiers.handlers.SpawnerUpgradeHandler;
import me.metallicgoat.tweaksaddon.gentiers.handlers.SuddenDeathHandler;

@Getter
public enum TierAction {
  GEN_UPGRADE(new SpawnerUpgradeHandler(), true),
  BED_DESTROY(new SuddenDeathHandler(), false),
  GAME_OVER(new RoundEndHandler(), false);

  private final String id;
  private final GenTierHandler handler;
  private final boolean messageSupported;

  TierAction(GenTierHandler handler, boolean messageSupported){
    this.id = TierAction.this.name().toLowerCase().replace("_", "-");
    this.handler = handler;
    this.messageSupported = messageSupported;
  }
}
