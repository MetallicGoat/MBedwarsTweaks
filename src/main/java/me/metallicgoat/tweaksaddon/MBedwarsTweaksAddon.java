package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAddon;
import me.metallicgoat.tweaksaddon.tweaks.hooks.*;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.*;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.*;
import me.metallicgoat.tweaksaddon.tweaks.explosives.*;
import me.metallicgoat.tweaksaddon.tweaks.misc.*;
import me.metallicgoat.tweaksaddon.tweaks.spawners.*;
import me.metallicgoat.tweaksaddon.tweaks.messages.*;
import me.metallicgoat.tweaksaddon.serverevents.*;
import org.bukkit.plugin.PluginManager;

public class MBedwarsTweaksAddon extends BedwarsAddon {

    private final MBedwarsTweaksPlugin plugin;

    public MBedwarsTweaksAddon(MBedwarsTweaksPlugin plugin) {
        super(plugin);

        this.plugin = plugin;
    }

    @Override
    public String getName(){
        return plugin.getName();
    }

    public static void registerEvents(){
        final MBedwarsTweaksPlugin plugin = MBedwarsTweaksPlugin.getInstance();
        final PluginManager manager = plugin.getServer().getPluginManager();

        // CONTRIBUTORS: PLEASE READ
        // NOTE: Please keep the following categories and classes in alphabetical order
        // NOTE: If you are adding support for your own plugin, please add your class to the hooks folder

        // Advanced Swords
        manager.registerEvents(new AlwaysSword(), plugin);
        manager.registerEvents(new AntiChest(), plugin);
        manager.registerEvents(new AntiDrop(), plugin);
        manager.registerEvents(new DegradingBuyGroups(), plugin);
        manager.registerEvents(new ReplaceSwordOnBuy(), plugin);
        manager.registerEvents(new ReplaceToolOnBuy(), plugin);
        manager.registerEvents(new ToolSwordHelper(), plugin);

        // Cosmetic
        manager.registerEvents(new ActionBar(), plugin);
        manager.registerEvents(new CustomTeamColors(), plugin);
        manager.registerEvents(new FinalStrike(), plugin);
        manager.registerEvents(new ForceScoreboardUpdating(), plugin);
        manager.registerEvents(new FriendlyVillagers(), plugin);
        manager.registerEvents(new HealPoolParticles(), plugin);
        manager.registerEvents(new SpongeParticles(), plugin);

        // Explosives
        manager.registerEvents(new DisableFireballOutsideArena(), plugin);
        manager.registerEvents(new FireballBlockBreakWhitelist(), plugin);
        manager.registerEvents(new FireballThrowEffects(), plugin);
        manager.registerEvents(new FireballUseCoolDown(), plugin);

        // Hooks
        manager.registerEvents(new PrestigesLevelOnExperienceBar(), plugin);

        // Messages
        manager.registerEvents(new BuyMessage(), plugin);
        manager.registerEvents(new CustomBedBreakMessage(), plugin);
        manager.registerEvents(new FinalKillSuffix(), plugin);
        manager.registerEvents(new TeamEliminate(), plugin);
        manager.registerEvents(new TopKillerMessage(), plugin);

        // Misc
        manager.registerEvents(new BlockArenaStats(), plugin);
        manager.registerEvents(new EmptyContainers(), plugin);
        manager.registerEvents(new GoldenGGListener(), plugin);
        manager.registerEvents(new HeightCap(), plugin);
        manager.registerEvents(new LeaveDelayListener(), plugin);
        manager.registerEvents(new LockTeamChest(), plugin);
        manager.registerEvents(new PermanentEffects(), plugin);
        manager.registerEvents(new PersonalChests(), plugin);
        manager.registerEvents(new PlayerLimitBypass(), plugin);
        manager.registerEvents(new PreventLiquidBuildUp(), plugin);
        manager.registerEvents(new RemoveInvisOnDamage(), plugin);

        // Spawners
        manager.registerEvents(new BedBreakTier(), plugin);
        manager.registerEvents(new DisableEmptyGenerators(), plugin);
        manager.registerEvents(new GenTiers(), plugin);
        manager.registerEvents(new SpawnerUpgrade(), plugin);

        // Server Events
        manager.registerEvents(new DependManager(), plugin);
        manager.registerEvents(new LoadConfigs(), plugin);
    }
}
