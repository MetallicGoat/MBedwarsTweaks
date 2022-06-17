package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.BedBreakTier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomBedBreakMessage implements Listener {
    @EventHandler
    public void onBedBreak(ArenaBedBreakEvent event){
        if(!ConfigValue.custom_bed_break_message)
            return;

        event.setBroadcasted(false);
        event.setPlayingSound(true);
        BedBreakTier.sendBedBreakMessage(event.getArena(), event.getTeam(), event.getPlayer());
    }
}
