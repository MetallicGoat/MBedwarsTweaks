package me.metallicgoat.tweaksaddon;

import lombok.Getter;

public enum DependType {
    PLACEHOLDER_API("PlaceholderAPI"),
    HOTBAR_MANAGER("MBedwarsHotbarManager"),
    PRESTIGE_ADDON("PrestigeAddon");

    @Getter
    private final String name;

    DependType(String name) {
        this.name = name;
    }

    public static DependType getTypeByName(String name){
        for(DependType type : DependType.values())
            if(type.name.equalsIgnoreCase(name))
                return type;

        return null;
    }
}
