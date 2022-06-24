package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAddon;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.*;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.*;
import me.metallicgoat.tweaksaddon.tweaks.gameplay.*;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.*;
import me.metallicgoat.tweaksaddon.tweaks.mechanics.*;
import me.metallicgoat.tweaksaddon.tweaks.messages.*;
import org.bukkit.plugin.PluginManager;

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

    public static void registerEvents(){

        final MBedwarsTweaksPlugin plugin = MBedwarsTweaksPlugin.getInstance();
        final PluginManager manager = plugin.getServer().getPluginManager();

        // Advanced Swords
        manager.registerEvents(new AlwaysSword(), plugin);
        manager.registerEvents(new AntiChest(), plugin);
        manager.registerEvents(new AntiDrop(), plugin);
        manager.registerEvents(new DowngradeTools(), plugin);
        manager.registerEvents(new OneSlotTools(), plugin);
        manager.registerEvents(new OrderedSwordBuy(), plugin);
        manager.registerEvents(new ReplaceSwordOnBuy(), plugin);
        manager.registerEvents(new SwordDrop(), plugin);
        manager.registerEvents(new ToolBuy(), plugin);

        // Cosmetic
        manager.registerEvents(new ActionBar(), plugin);
        manager.registerEvents(new CustomTeamColors(), plugin);
        manager.registerEvents(new FinalStrike(), plugin);
        manager.registerEvents(new SpongeParticles(), plugin);

        // Gameplay
        manager.registerEvents(new DisableEmptyGenerators(), plugin);
        manager.registerEvents(new EmptyContainers(), plugin);
        manager.registerEvents(new HeightCap(), plugin);
        manager.registerEvents(new LockTeamChest(), plugin);
        manager.registerEvents(new PermanentEffects(), plugin);
        manager.registerEvents(new PersonalChests(), plugin);

        // GenTiers
        manager.registerEvents(new BedBreakTier(), plugin);
        manager.registerEvents(new GenTiers(), plugin);

        // Mechanics
        manager.registerEvents(new DisableFireballOutsideArena(), plugin);
        manager.registerEvents(new FireballBlockBreakWhitelist(), plugin);
        manager.registerEvents(new PreventLiquidBuildUp(), plugin);
        manager.registerEvents(new RemoveInvisOnDamage(), plugin);

        // Messages
        manager.registerEvents(new BuyMessage(), plugin);
        manager.registerEvents(new CustomBedBreakMessage(), plugin);
        manager.registerEvents(new FinalKillSuffix(), plugin);
        manager.registerEvents(new TeamEliminate(), plugin);
        manager.registerEvents(new TopKillerMessage(), plugin);

    }
}
