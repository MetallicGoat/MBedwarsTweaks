package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAddon;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.AlwaysSword;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.AntiChest;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.AntiDrop;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.DegradingBuyGroups;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.ReplaceSwordOnBuy;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.ReplaceToolOnBuy;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.ToolSwordHelper;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.ActionBar;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.CustomTeamColors;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.ForceScoreboardUpdating;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.FriendlyVillagers;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.HealPoolParticles;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.SpongeParticles;
import me.metallicgoat.tweaksaddon.tweaks.explosives.DisableFireballOutsideArena;
import me.metallicgoat.tweaksaddon.tweaks.explosives.FireballBlockBreakWhitelist;
import me.metallicgoat.tweaksaddon.tweaks.explosives.FireballThrowEffects;
import me.metallicgoat.tweaksaddon.tweaks.explosives.FireballUseCoolDown;
import me.metallicgoat.tweaksaddon.tweaks.hooks.PrestigesLevelOnExperienceBar;
import me.metallicgoat.tweaksaddon.tweaks.messages.BuyMessage;
import me.metallicgoat.tweaksaddon.tweaks.messages.CustomBedBreakMessage;
import me.metallicgoat.tweaksaddon.tweaks.messages.FinalKillSuffix;
import me.metallicgoat.tweaksaddon.tweaks.messages.TeamEliminate;
import me.metallicgoat.tweaksaddon.tweaks.messages.TopKillerMessage;
import me.metallicgoat.tweaksaddon.tweaks.misc.BlockArenaStats;
import me.metallicgoat.tweaksaddon.tweaks.misc.EmptyContainers;
import me.metallicgoat.tweaksaddon.tweaks.misc.HeightCap;
import me.metallicgoat.tweaksaddon.tweaks.misc.LockTeamChest;
import me.metallicgoat.tweaksaddon.tweaks.misc.PermanentEffects;
import me.metallicgoat.tweaksaddon.tweaks.misc.PersonalChests;
import me.metallicgoat.tweaksaddon.tweaks.misc.PlayerLimitBypass;
import me.metallicgoat.tweaksaddon.tweaks.misc.PreventLiquidBuildUp;
import me.metallicgoat.tweaksaddon.tweaks.misc.RemoveInvisOnDamage;
import me.metallicgoat.tweaksaddon.tweaks.spawners.BedBreakTier;
import me.metallicgoat.tweaksaddon.tweaks.spawners.DisableEmptyGenerators;
import me.metallicgoat.tweaksaddon.tweaks.spawners.GenTiers;
import me.metallicgoat.tweaksaddon.tweaks.spawners.SpawnerUpgrade;
import org.bukkit.plugin.PluginManager;

public class MBedwarsTweaksAddon extends BedwarsAddon {

  private final MBedwarsTweaksPlugin plugin;

  public MBedwarsTweaksAddon(MBedwarsTweaksPlugin plugin) {
    super(plugin);

    this.plugin = plugin;
  }

  public static void registerEvents() {
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
    manager.registerEvents(new HeightCap(), plugin);
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
  }

  @Override
  public String getName() {
    return plugin.getName();
  }
}
