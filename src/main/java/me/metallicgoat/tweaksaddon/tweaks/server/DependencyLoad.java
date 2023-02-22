package me.metallicgoat.tweaksaddon.tweaks.server;

import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class DependencyLoad implements Listener {

    private final MBedwarsTweaksPlugin plugin;

    public DependencyLoad(MBedwarsTweaksPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onDependLoad(PluginEnableEvent event){
        if(event.getPlugin().getName().equals("MBedwarsHotbarManager"))
            plugin.setHotbarManagerEnabled(true);

        if(event.getPlugin().getName().equals("PrestigeAddon"))
            plugin.setPrestigesAddonEnabled(true);
    }

    @EventHandler
    public void onDependUnload(PluginDisableEvent event){
        if(event.getPlugin().getName().equals("MBedwarsHotbarManager"))
            plugin.setHotbarManagerEnabled(false);

        if(event.getPlugin().getName().equals("PrestigeAddon"))
            plugin.setPrestigesAddonEnabled(false);
    }
}
