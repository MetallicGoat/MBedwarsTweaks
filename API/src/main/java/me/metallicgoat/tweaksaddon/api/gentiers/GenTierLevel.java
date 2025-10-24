package me.metallicgoat.tweaksaddon.api.gentiers;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.api.message.Message;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.metallicgoat.tweaksaddon.api.unsafe.MBedwarsTweaksAPILayer;
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
  private final GenTierHandler handler; // Action (eg bed break or upgrade)
  private final Duration time; // Time until the update happens (After Last Event)
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
      GenTierHandler handler,
      Duration time,
      @Nullable String earnMessage,
      @Nullable Sound earnSound) {

    this(tier,
        tierName,
        null,
        null,
        handler,
        time,
        null,
        null,
        earnMessage,
        earnSound);
  }

  /**
   * Plays the sound, and broadcasts the message of this gen tier.
   * Used when this level becomes active
   *
   * @param arena where this level is active on
   */
  public void broadcastEarn(Arena arena) {
    if (this.earnSound != null) {
      for (Player p : arena.getPlayers())
        p.playSound(p.getLocation(), this.earnSound, 1F, 1F);
    }

    if (this.earnMessage != null)
      arena.broadcast(Message.build(this.earnMessage));

  }

  /**
   * Returns the DropType of the spawners this level will affect
   * Will return <code>null</code> is level does not affect as spawner (i.e. Bread break tier)
   *
   * @return the DropType this level aims to affect if applicable
   */
  @Nullable
  public DropType getType() {
    return GameAPI.get().getDropTypeById(this.typeId);
  }

  /**
   * Gets the next level after this one has been reached.
   *
   * @return the next GenTierLevel, or <code>null</code> if this is the highest level
   */
  @Nullable
  public GenTierLevel getNextLevel() {
    return MBedwarsTweaksAPILayer.INSTANCE.getGenTierLevel(this.tier + 1);
  }

  /**
   * Gets the previous level before this one has been reached.
   *
   * @return the previous GenTierLevel, or <code>null</code> if this is the first level
   */
  @Nullable
  public GenTierLevel getPreviousLevel() {
    return MBedwarsTweaksAPILayer.INSTANCE.getGenTierLevel(this.tier - 1);
  }
}