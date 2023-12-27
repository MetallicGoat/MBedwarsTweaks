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
  private final TierAction action;
  private final double time;
  @Nullable
  private final String holoName;
  @Nullable
  private final String typeId;
  @Nullable
  private final Double speed;
  @Nullable
  private final Integer limit;
  @Nullable
  private final String earnMessage;
  @Nullable
  private final Sound earnSound;

  public GenTierLevel(
      String tierName,
      TierAction action,
      double time,
      @Nullable String earnMessage,
      @Nullable Sound earnSound
  ) {
    this.tierName = tierName;
    this.holoName = null;
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
      @Nullable String holoName, // Example '&eTier &cII'
      @Nullable String typeId, // Spawners with this drop-type should update
      TierAction action, // Action (eg bed break or upgrade)
      double time, // Time until the update happens (After Last Event)
      @Nullable Double speed, // New drop speed
      @Nullable Integer limit, // New drop speed
      @Nullable String earnMessage, // The chat message displayed on update
      @Nullable Sound earnSound // Sound played when a tier is earned
  ) {
    this.tierName = tierName;
    this.holoName = holoName;
    this.typeId = typeId;
    this.action = action;
    this.time = time;
    this.speed = speed;
    this.limit = limit;
    this.earnMessage = earnMessage;
    this.earnSound = earnSound;
  }

  public void broadcastEarn(Arena arena, boolean messageSupported) {
    if (this.earnSound != null) {
      for (Player p : arena.getPlayers())
        p.playSound(p.getLocation(), this.earnSound, 1F, 1F);
    }

    if (messageSupported && this.earnMessage != null)
      arena.broadcast(Message.build(this.earnMessage));
    
  }

  public DropType getType() {
    return GameAPI.get().getDropTypeById(this.typeId);
  }
}