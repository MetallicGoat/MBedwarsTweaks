package me.metallicgoat.tweaksaddon.gentiers.dragons;

import de.marcely.bedwars.api.event.player.PlayerTriggerUpgradeEvent;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.gentiers.GenTiers;

public class SuddenDeathUpgrade extends UpgradeTriggerHandler {
  public SuddenDeathUpgrade() {
    super("sudden-death", false, MBedwarsTweaksPlugin.getInstance());
  }

  @Override
  public void onTrigger(PlayerTriggerUpgradeEvent event) {
    GenTiers.getState(event.getArena()).addDragonTeam(event.getTeam());
  }
}
