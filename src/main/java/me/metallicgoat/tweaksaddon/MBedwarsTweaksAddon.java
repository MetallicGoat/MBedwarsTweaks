package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAddon;

public class MBedwarsTweaksAddon extends BedwarsAddon {
    private final MBedwarsTweaksPlugin plugin;

    public MBedwarsTweaksAddon(MBedwarsTweaksPlugin plugin) {
        super(plugin);

        this.plugin = plugin;
    }

    @Override
    public String getName(){
        return "MBedwarsTweaks";
    }
}
