package me.metallicgoat.tweaksaddon.gentiers;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.api.message.Message;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@Getter
public class GenTierLevel {

  private final String tierName;
  private final String tierLevel;
  private final String typeId;
  private final TierAction action;
  private final double time;
  @Nullable
  private final Double speed;
  @Nullable
  private final Integer limit;

  private final String earnMessage;
  private final Sound earnSound;

  public GenTierLevel(
      String tierName,
      String tierLevel,
      TierAction action,
      double time,
      @Nullable String earnMessage,
      @Nullable Sound earnSound
  ) {
    this.tierName = tierName;
    this.tierLevel = tierLevel;
    this.typeId = null;
    this.action = action;
    this.time = time;
    this.speed = null;
    this.limit = null;
    this.earnMessage = earnMessage;
    this.earnSound = earnSound;
  }

  public GenTierLevel(
      String tierName, // Display Name
      String tierLevel, // Example '&eTier &cII'
      String typeId, // Spawners with this drop-type should update
      TierAction action, // Action (eg bed break or upgrade)
      double time, // Time until the update happens (After Last Event)
      @Nullable Double speed, // New drop speed
      @Nullable Integer limit, // New drop speed
      @Nullable String earnMessage, // The chat message displayed on update
      @Nullable Sound earnSound // Sound played when a tier is earned
  ) {
    this.tierName = tierName;
    this.tierLevel = tierLevel;
    this.typeId = typeId;
    this.action = action;
    this.time = time;
    this.speed = speed;
    this.limit = limit;
    this.earnMessage = earnMessage;
    this.earnSound = earnSound;
  }

  public void broadcastEarn(Arena arena, boolean messageSupported) {
    if (earnSound != null) {
      for (Player p : arena.getPlayers())
        p.playSound(p.getLocation(), earnSound, 1F, 1F);
    }

    if (messageSupported && earnMessage != null)
      arena.broadcast(Message.build(earnMessage));
    
  }

  public DropType getType() {
    return GameAPI.get().getDropTypeById(typeId);
  }
}