package me.metallicgoat.tweaksaddon.tweaks.gentiers;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.game.spawner.DropType;
import lombok.Getter;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

public class GenTierLevel {

    @Getter private final String tierName;
    @Getter private final String tierLevel;
    @Getter private final String typeId;
    @Getter private final TierAction action;
    @Getter private final long time;
    @Getter @Nullable private final Double speed;
    //@Getter @Nullable private final Integer limit;
    @Getter private final String earnMessage;
    @Getter private final Sound earnSound;

    public GenTierLevel(
            String tierName,
            String tierLevel,
            TierAction action,
            long time,
            String earnMessage,
            Sound earnSound
    ) {
        this.tierName = tierName;
        this.tierLevel = tierLevel;
        this.typeId = null;
        this.action = action;
        this.time = time;
        this.speed = null;
        //this.limit = null;
        this.earnMessage = earnMessage;
        this.earnSound = earnSound;
    }

    public GenTierLevel(
            String tierName, // Display Name
            String tierLevel, // Example '&eTier &cII'
            String typeId, // Spawners with this drop-type should update
            TierAction action, // Action (eg bed break or upgrade)
            long time, // Time until the update happens (After Last Event)
            @Nullable Double speed, // New drop speed
            @Nullable Integer limit, // New drop speed
            String earnMessage, // The chat message displayed on update
            Sound earnSound // Sound played when a tier is earned
    ) {
        this.tierName = tierName;
        this.tierLevel = tierLevel;
        this.typeId = typeId;
        this.action = action;
        this.time = time;
        this.speed = speed;
        //this.limit = limit;
        this.earnMessage = earnMessage;
        this.earnSound = earnSound;
    }

    public DropType getType(){
        return GameAPI.get().getDropTypeById(typeId);
    }
}