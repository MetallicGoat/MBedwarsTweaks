package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;

import java.util.Collection;
import java.util.Collections;

public class Util {
    public static Collection<Arena> parseArenas(String arenaKey){

        if(arenaKey.equalsIgnoreCase("ALL-ARENAS")){
            return GameAPI.get().getArenas();
        }

        final Arena arenaByName = GameAPI.get().getArenaByName(arenaKey);

        if(arenaByName != null)
            return Collections.singleton(arenaByName);

        try {return GameAPI.get().getArenasByPickerCondition(arenaKey);}
        catch (Exception ignored) {}

        return Collections.emptyList();
    }
}
