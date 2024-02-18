package me.metallicgoat.tweaksaddon.gentiers;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.api.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
public class GenTierLevel {

  private final int tier;
  private final String tierName; // Display Name
  @Nullable
  private final String holoName; // Example '&eTier &cII'
  @Nullable
  private final String typeId; // Spawners with this drop-type should update
  private final TierAction action; // Action (eg bed break or upgrade)
  private final double time; // Time until the update happens (After Last Event)
  @Nullable
  private final Double speed; // New drop speed
  @Nullable
  private final Integer limit; // New drop speed
  @Nullable
  private final String earnMessage; // The chat message displayed on update
  @Nullable
  private final Sound earnSound; // Sound played when a tier is earned

  public GenTierLevel(
      int tier,
      String tierName,
      TierAction action,
      double time,
      @Nullable String earnMessage,
      @Nullable Sound earnSound) {

    this(tier,
        tierName,
        null,
        null,
        action,
        time,
        null,
        null,
        earnMessage,
        earnSound);
  }

  public void broadcastEarn(Arena arena, boolean messageSupported) {
    if (this.earnSound != null) {
      for (Player p : arena.getPlayers())
        p.playSound(p.getLocation(), this.earnSound, 1F, 1F);
    }

    if (messageSupported && this.earnMessage != null)
      arena.broadcast(Message.build(this.earnMessage));

  }

  public @Nullable DropType getType() {
    return GameAPI.get().getDropTypeById(this.typeId);
  }
}