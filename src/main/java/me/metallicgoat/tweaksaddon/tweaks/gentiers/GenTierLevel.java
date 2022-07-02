package me.metallicgoat.tweaksaddon.tweaks.gentiers;

import de.marcely.bedwars.api.game.spawner.DropType;
import lombok.Getter;

public class GenTierLevel {

    @Getter private final String tierName;
    @Getter private final String tierLevel;
    @Getter private final DropType type;
    @Getter private final TierAction action;
    @Getter private final Long time;
    @Getter private final Double speed;
    @Getter private final String earnMessage;

    public GenTierLevel(
            String tierName,
            TierAction action,
            long time
    ) {
        this.tierName = tierName;
        this.tierLevel = null;
        this.type = null;
        this.action = action;
        this.time = time;
        this.speed = null;
        this.earnMessage = null;
    }

    public GenTierLevel(
            String tierName, // Display Name
            String tierLevel, // Example '&eTier &cII'
            DropType type, // Spawners with this drop-type should update
            TierAction action,
            long time, // Time until the update happens (After Last Event)
            double speed, // New drop speed
            String earnMessage // The chat message displayed on update
    ) {
        this.tierName = tierName;
        this.tierLevel = tierLevel;
        this.type = type;
        this.action = action;
        this.time = time;
        this.speed = speed;
        this.earnMessage = earnMessage;
    }
}