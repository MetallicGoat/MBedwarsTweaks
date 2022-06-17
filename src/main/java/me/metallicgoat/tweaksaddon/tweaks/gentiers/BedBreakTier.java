package me.metallicgoat.tweaksaddon.tweaks.gentiers;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.location.XYZD;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BedBreakTier implements Listener {

    // Sets all beds to be destroyed at a specified time
    public static void breakArenaBeds(Arena arena){
        // Break all beds in an arena
        for (Team team : arena.getEnabledTeams()) {
            final XYZD bedLoc = arena.getBedLocation(team);
            if (!arena.isBedDestroyed(team) && bedLoc != null) {
                arena.destroyBedNaturally(team, "Auto-Break", !ConfigValue.custom_bed_break_message);
                bedLoc.toLocation(arena.getGameWorld()).getBlock().setType(Material.AIR);
                if(ConfigValue.custom_bed_break_message)
                    sendBedBreakMessage(arena, team, null);
            }
        }
        // Broadcast Message
        if(ConfigValue.auto_destroy_bed_message_enabled) {
            for (String s : ConfigValue.auto_destroy_bed_message) {
                arena.broadcast(Message.build(s).done());
            }
        }
    }

    // Message that gets sent when a bed gets destroyed
    public static void sendBedBreakMessage(Arena arena, Team team, Player destroyer){

        // Send title to victim team
        for(Player p : arena.getPlayersInTeam(team)){
            BedwarsAPI.getNMSHelper().showTitle(p,
                    ChatColor.translateAlternateColorCodes('&', ConfigValue.bed_destroy_title),
                    ChatColor.translateAlternateColorCodes('&', ConfigValue.bed_destroy_subtitle),
                    60, 15, 15);
        }

        // Send public message
        if(destroyer == null)
            return;

        // Send public message
        for (String message : ConfigValue.player_break_bed_message) {
            final String teamName = team.getDisplayName();
            final String playerName = BedwarsAPI.getHelper().getPlayerDisplayName(destroyer);
            final String destroyerColor = arena.getPlayerTeam(destroyer) != null ? "&" + arena.getPlayerTeam(destroyer).getChatColor().getChar() : "";
            final String destroyerTeam = arena.getPlayerTeam(destroyer) != null ? ChatColor.stripColor(arena.getPlayerTeam(destroyer).getDisplayName()) : "";

            final String messageFormatted = Message.build(message)
                    .placeholder("team-name", teamName)
                    .placeholder("team-color", "&" + team.getChatColor().getChar())
                    .placeholder("destroyer-name", playerName)
                    .placeholder("destroyer-team-name", destroyerTeam)
                    .placeholder("destroyer-color", destroyerColor)
                    .done();
            arena.broadcast(messageFormatted);
        }
    }
}
