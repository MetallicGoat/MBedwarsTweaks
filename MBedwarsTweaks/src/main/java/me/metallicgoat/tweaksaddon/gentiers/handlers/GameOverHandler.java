package me.metallicgoat.tweaksaddon.gentiers.handlers;

import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierActionType;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierLevel;

public class GameOverHandler extends BaseGenTierHandler {

  @Override
  public void run(GenTierLevel level, Arena arena) {

  }

  @Override
  public String getId() {
    return GenTierActionType.GAME_OVER.getDefaultHandlerId();
  }

  @Override
  public GenTierActionType getActionType() {
    return GenTierActionType.GAME_OVER;
  }
}
