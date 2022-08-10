package me.metallicgoat.tweaksaddon.tweaks.server;

import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginLoad implements Listener {

    @EventHandler
    public void onDependLoad(PluginEnableEvent event){

        if(!event.getPlugin().getName().equals("MBedwarsHotbarManager"))
            return;

        MBedwarsTweaksPlugin.getInstance().setHotbarManagerEnabled(true);

    }

    @EventHandler
    public void onDependLoad(PluginDisableEvent event){

        if(!event.getPlugin().getName().equals("MBedwarsHotbarManager"))
            return;

        MBedwarsTweaksPlugin.getInstance().setHotbarManagerEnabled(false);

    }
}
