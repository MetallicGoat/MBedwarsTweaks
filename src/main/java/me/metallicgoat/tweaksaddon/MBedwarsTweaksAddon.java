package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAddon;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.message.DefaultMessageMappings;
import de.marcely.bedwars.api.message.MessageAPI;
import me.metallicgoat.tweaksaddon.config.ConfigLoader;
import me.metallicgoat.tweaksaddon.gentiers.dragons.SuddenDeathUpgrade;
import me.metallicgoat.tweaksaddon.integration.DependencyLoader;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.*;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.*;
import me.metallicgoat.tweaksaddon.tweaks.explosives.*;
import me.metallicgoat.tweaksaddon.tweaks.messages.*;
import me.metallicgoat.tweaksaddon.tweaks.misc.*;
import me.metallicgoat.tweaksaddon.tweaks.spawners.*;
import me.metallicgoat.tweaksaddon.gentiers.GenTiers;
import org.bukkit.plugin.PluginManager;

public class MBedwarsTweaksAddon extends BedwarsAddon {

  private final MBedwarsTweaksPlugin plugin;

  public MBedwarsTweaksAddon(MBedwarsTweaksPlugin plugin) {
    super(plugin);

    this.plugin = plugin;
  }

  public void registerMessageMappings() {
    try {
      MessageAPI.get().registerDefaultMappings(
          DefaultMessageMappings.loadInternalYAML(this.plugin, this.plugin.getResource("messages.yml"))
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void registerEvents() {
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
    manager.registerEvents(new FriendlyVillagers(), plugin);
    manager.registerEvents(new HealPoolParticles(), plugin);
    manager.registerEvents(new InvisFootstepsParticles(), plugin);
    manager.registerEvents(new PlaceholderUpdating(), plugin);
    manager.registerEvents(new SpongeParticles(), plugin);

    // Explosives
    manager.registerEvents(new DisableFireballOutsideArena(), plugin);
    manager.registerEvents(new ExplosiveFallDamageMultiplier(), plugin);
    manager.registerEvents(new FireballBlockBreakWhitelist(), plugin);
    manager.registerEvents(new FireballThrowEffects(), plugin);
    manager.registerEvents(new FireballUseCoolDown(), plugin);
    manager.registerEvents(new TNTIgniteCountdown(), plugin);

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
    manager.registerEvents(new PlaceBlocksOnBed(), plugin);
    manager.registerEvents(new PlayerLimitBypass(), plugin);
    manager.registerEvents(new RemoveInvisOnDamage(), plugin);
    manager.registerEvents(new SpecialItemCooldown(), plugin);
    manager.registerEvents(new WorldBorderResize(), plugin);
    //manager.registerEvents(new TieBreaker(), plugin);

    // Spawners
    manager.registerEvents(new AFKSpawners(), plugin);
    manager.registerEvents(new DisableEmptyGenerators(), plugin);
    manager.registerEvents(new SpawnerUpgrade(), plugin);

    // Gen Tiers
    manager.registerEvents(new GenTiers(), plugin);

    // Server Events
    manager.registerEvents(new ConfigLoader(), plugin);
    manager.registerEvents(new DependencyLoader(), plugin);
  }

  public void registerUpgrades() {
    GameAPI.get().registerUpgradeTriggerHandler(new SuddenDeathUpgrade());
  }

  @Override
  public String getName() {
    return plugin.getName();
  }
}
