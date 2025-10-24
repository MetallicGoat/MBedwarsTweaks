package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.VarParticle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import me.metallicgoat.tweaksaddon.utils.CachedArenaIdentifier;
import me.metallicgoat.tweaksaddon.utils.Console;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Deprecated
public class ConfigLegacyMigrator {

  public static void loadOldMain(FileConfiguration config) {
    MainConfig.gen_tiers_enabled = config.getBoolean("Gen-Tiers-Enabled", false);
    MainConfig.gen_tiers_custom_holo_enabled = config.getBoolean("Gen-Tiers-Holos-Enabled", false);

    MainConfig.gen_tiers_custom_holo_enabled = config.getBoolean("Gen-Tiers-Holos-Enabled", MainConfig.gen_tiers_custom_holo_enabled);

    MainConfig.gen_tiers_start_tier = config.getString("Tier-One-Titles.Tier-Name", MainConfig.gen_tiers_start_tier);
    {
      if (config.contains("Tier-One-Titles.Spawners")) {
        MainConfig.gen_tiers_start_spawners.clear();

        for (String string : config.getStringList("Tier-One-Titles.Spawners")) {

          final DropType type = Util.getDropType(string);

          if (type != null)
            MainConfig.gen_tiers_start_spawners.add(type);

        }
      }
    }

    if (config.contains("Spawner-Title"))
      MainConfig.gen_tiers_custom_holo_titles = config.getStringList("Spawner-Title");

    MainConfig.custom_action_bar_in_game = config.getBoolean("Action-Bar.Enabled-In-Game", false);
    MainConfig.custom_action_bar_in_lobby = config.getBoolean("Action-Bar.Enabled-In-Lobby", false);
    MainConfig.custom_action_bar_message = config.getString("Action-Bar.Message", MainConfig.custom_action_bar_message);

    MainConfig.final_kill_suffix_enabled = config.getBoolean("Final-Kill-Suffix.Enabled", true);
    MainConfig.final_kill_suffix = config.getString("Final-Kill-Suffix.Suffix", MainConfig.final_kill_suffix);

    MainConfig.buy_message_enabled = config.getBoolean("Buy-Message.Enabled", false);
    MainConfig.buy_message = config.getString("Buy-Message.Message", MainConfig.buy_message);

    MainConfig.custom_bed_break_message_enabled = config.getBoolean("Player-Bed-Break-Message.Enabled", true);
    if (config.contains("Player-Bed-Break-Message.Message"))
      MainConfig.custom_bed_break_message = config.getStringList("Player-Bed-Break-Message.Message");

    MainConfig.auto_bed_break_message_enabled = config.getBoolean("Auto-Bed-Break-Message.Enabled", true);
    if (config.contains("Auto-Bed-Break-Message.Message"))
      MainConfig.auto_bed_break_message = config.getStringList("Auto-Bed-Break-Message.Message");

    MainConfig.bed_destroy_title_enabled = config.getBoolean("Bed-Destroy-Title.Enabled", true);
    MainConfig.bed_destroy_title = config.getString("Bed-Destroy-Title.BigTitle", MainConfig.bed_destroy_title);
    MainConfig.bed_destroy_subtitle = config.getString("Bed-Destroy-Title.SubTitle", MainConfig.bed_destroy_subtitle);

    MainConfig.team_eliminate_message_enabled = config.getBoolean("Team-Eliminate.Enabled", true);
    if (config.contains("Team-Eliminate.Message"))
      MainConfig.team_eliminate_message = config.getStringList("Team-Eliminate.Message");

    MainConfig.top_killer_message_enabled = config.getBoolean("Top-Killer-Message.Enabled", false);
    if (config.contains("Top-Killer-Message.Pre-Lines"))
      MainConfig.top_killer_pre_lines = config.getStringList("Top-Killer-Message.Pre-Lines");
    {
      final ConfigurationSection section = config.getConfigurationSection("Top-Killer-Message.Lines");

      if (section != null) {

        final HashMap<Integer, String> map = new HashMap<>();

        MainConfig.top_killer_lines.clear();

        for (String key : section.getKeys(false)) {
          final Integer placeValue = Helper.get().parseInt(key);

          if (placeValue == null)
            continue;

          final String line = config.getString("Top-Killer-Message.Lines." + key);

          if (line != null)
            map.put(placeValue, line);

        }

        MainConfig.top_killer_lines = map;
      }
    }

    if (config.contains("Top-Killer-Message.Sub-Lines"))
      MainConfig.top_killer_sub_lines = config.getStringList("Top-Killer-Message.Sub-Lines");

    MainConfig.no_top_killer_message_enabled = config.getBoolean("No-Top-Killer-Message.Enabled", false);
    if (config.contains("No-Top-Killer-Message.Message"))
      MainConfig.no_top_killer_message = config.getStringList("No-Top-Killer-Message.Message");

    MainConfig.papi_next_tier_lobby_waiting = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Waiting", MainConfig.papi_next_tier_lobby_waiting);
    MainConfig.papi_next_tier_lobby_starting = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Starting", MainConfig.papi_next_tier_lobby_starting);
    MainConfig.papi_next_tier_lobby_running = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Running", MainConfig.papi_next_tier_lobby_running);
    MainConfig.papi_next_tier_lobby_end_lobby = config.getString("Next-Tier-PAPI-Placeholder.Lobby-End-Lobby", MainConfig.papi_next_tier_lobby_end_lobby);
    MainConfig.papi_next_tier_lobby_resetting = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Resetting", MainConfig.papi_next_tier_lobby_resetting);
    MainConfig.papi_next_tier_lobby_stopped = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Stopped", MainConfig.papi_next_tier_lobby_stopped);

    {
      if (config.contains("PAPI-Arena-Modes")) {

        final HashMap<String, String> map = new HashMap<>();

        for (String string : config.getStringList("PAPI-Arena-Modes")) {
          if (string.contains(":")) {
            final String[] strings = string.split(":");

            map.put(strings[0], strings[1]);
          }
        }

        MainConfig.papi_arena_mode = map;
      }
    }

    MainConfig.papi_count_spectators_as_players = config.getBoolean("Player-Count-Placeholder-Count-Spectators", false);

    MainConfig.papi_team_status_has_bed = config.getString("Team-Status-Placeholder.Has-Bed", MainConfig.papi_team_status_has_bed);
    MainConfig.papi_team_status_no_bed = config.getString("Team-Status-Placeholder.No-Bed", MainConfig.papi_team_status_no_bed);
    MainConfig.papi_team_status_team_dead = config.getString("Team-Status-Placeholder.Team-Dead", MainConfig.papi_team_status_team_dead);
    MainConfig.papi_team_status_your_team_suffix = config.getString("Team-Status-Placeholder.Your-Team", MainConfig.papi_team_status_your_team_suffix);

    MainConfig.papi_team_you_placeholder = config.getString("Team-You-Placeholder", MainConfig.papi_team_you_placeholder);

    MainConfig.scoreboard_updating_enabled_in_game = config.getBoolean("Force-Scoreboard-Updating.Enabled-In-Game", false);
    MainConfig.scoreboard_updating_enabled_in_lobby = config.getBoolean("Force-Scoreboard-Updating.Enabled-In-Lobby", false);

    MainConfig.fireball_whitelist_enabled = config.getBoolean("FireballWhitelist.Enabled", false);
    {
      if (config.contains("FireballWhitelist.Blocks")) {

        final List<Material> mats = new ArrayList<>();

        for (String blockName : config.getStringList("FireballWhitelist.Blocks")) {

          final Material mat = Helper.get().getMaterialByName(blockName);

          if (mat != null)
            mats.add(mat);

        }

        MainConfig.fireball_whitelist_blocks = mats;
      }
    }

    MainConfig.fireball_cooldown_enabled = config.getBoolean("Fireball-Cooldown.Enabled", true);
    MainConfig.fireball_cooldown_time = config.getLong("Fireball-Cooldown.Time", 20L);

    MainConfig.fireball_throw_effects_enabled = config.getBoolean("Fireball-Throw-Effects.Enabled", true);
    {
      if (config.getStringList("Fireball-Throw-Effects.Effects") != null) {
        MainConfig.fireball_throw_effects = new ArrayList<>();

        for (String raw : config.getStringList("Fireball-Throw-Effects.Effects")) {
          final String[] parts = raw.split(":");

          if (parts.length == 3) {
            if (Util.isInteger(parts[1]) && Util.isInteger(parts[2])) {
              final PotionEffectType type = PotionEffectType.getByName(parts[0]);

              if (type != null) {
                MainConfig.fireball_throw_effects.add(new PotionEffect(type, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]) - 1));
              } else
                Console.printConfigWarn("\"Fireball-Throw-Effects.Effects\": " + parts[0] + " isn't a valid effect type", "Main");
            } else
              Console.printConfigWarn("\"Fireball-Throw-Effects.Effects\": " + parts[1] + " or " + parts[2] + " isn't a valid number", "Main");
          } else
            Console.printConfigWarn("\"Fireball-Throw-Effects.Effects\": \"" + raw + "\" has " + parts.length + " :, but 3 are needed", "Main");
        }
      }
    }

    MainConfig.friendly_villagers_enabled = config.getBoolean("Friendly-Villagers.Enabled", MainConfig.friendly_villagers_enabled);
    MainConfig.friendly_villagers_range = config.getInt("Friendly-Villagers.Range", MainConfig.friendly_villagers_range);
    MainConfig.friendly_villagers_check_visibility = config.getBoolean("Friendly-Villagers.Check-Visibility", MainConfig.friendly_villagers_check_visibility);

    MainConfig.disable_empty_generators = config.getBoolean("Disable-Unused-Gens.Enabled", false);
    MainConfig.disable_empty_generators_range = config.getDouble("Disable-Unused-Gens.Range", MainConfig.disable_empty_generators_range);
    {
      if (config.contains("Disable-Unused-Gens.Gen-Types")) {

        final List<DropType> dropTypes = new ArrayList<>();

        for (String string : config.getStringList("Disable-Unused-Gens.Gen-Types")) {
          final DropType type = Util.getDropType(string);

          if (type != null)
            dropTypes.add(type);
        }

        MainConfig.disable_empty_generators_spawners = dropTypes;
      }
    }

    MainConfig.advanced_forge_enabled = config.getBoolean("Advanced-Forge-Upgrade.Enabled");
    MainConfig.advanced_forge_range = config.getInt("Advanced-Forge-Upgrade.Range", 20);
    MainConfig.advanced_forge_level = config.getInt("Advanced-Forge-Upgrade.Upgrade-Level", 3);
    MainConfig.advanced_forge_drop_rate = config.getInt("Advanced-Forge-Upgrade.New-Drop-Rate", 15);
    MainConfig.advanced_forge_new_drop = config.getString("Advanced-Forge-Upgrade.New-Drop-Type", MainConfig.advanced_forge_new_drop);
    MainConfig.advanced_forge_effected_spawner = config.getString("Advanced-Forge-Upgrade.Effected-Spawner", MainConfig.advanced_forge_effected_spawner);

    MainConfig.lock_team_chest_enabled = config.getBoolean("Lock-Team-Chest.Enabled", false);
    MainConfig.lock_team_chest_range = config.getInt("Lock-Team-Chest.Range", 10);
    MainConfig.lock_team_chest_fail_open = config.getString("Lock-Team-Chest.Fail-Open", MainConfig.lock_team_chest_fail_open);

    MainConfig.personal_ender_chests_enabled = config.getBoolean("Personal-Ender-Chests.Enabled", false);

    MainConfig.player_limit_bypass = config.getBoolean("Bypass-PlayerLimit", false);

    MainConfig.remove_invis_ondamage_enabled = config.getBoolean("Break-Invis.Enabled", true);
    {
      if (config.contains("Break-Invis.Causes")) {

        final List<EntityDamageEvent.DamageCause> damageCauses = new ArrayList<>();

        for (String string : config.getStringList("Break-Invis.Causes")) {

          try {
            damageCauses.add(EntityDamageEvent.DamageCause.valueOf(string));
          } catch (IllegalArgumentException exception) {
            // Log failure to parse
          }
        }

        MainConfig.remove_invis_damge_causes = damageCauses;
      }
    }

    MainConfig.permanent_effects_enabled = config.getBoolean("Permanent-Effects.Enabled", false);
    loadPermanentEffects(config, "Permanent-Effects.Effects");

    MainConfig.block_stat_change_enabled = config.getBoolean("Block-Stat-Change.Enabled");
    {
      final List<String> blockedArenas = config.getStringList("Block-Stat-Change.Arenas");

      if (blockedArenas != null) {
        MainConfig.block_stat_change_arenas = blockedArenas.stream().map(CachedArenaIdentifier::new).collect(Collectors.toList());
      }
    }

    MainConfig.sponge_particles_enabled = config.getBoolean("Sponge-Particles", true);

    MainConfig.heal_pool_particle_enabled = config.getBoolean("Heal-Pool-Particles.Enabled", true);
    MainConfig.heal_pool_particle_team_view_only = config.getBoolean("Heal-Pool-Particles.Team-View-Only", true);
    MainConfig.prestiges_level_on_exp_bar = config.getBoolean("Bedwars-Level-On-Experience-Bar", false);
    MainConfig.heal_pool_particle_range = config.getInt("Heal-Pool-Particles.Range", 15);
    {
      final String particleName = config.getString("Heal-Pool-Particles.Particle");

      if (particleName != null) {
        try {
          MainConfig.heal_pool_particle_type = VarParticle.newInstanceByName(particleName);
        } catch (IllegalArgumentException exception) {
          Console.printConfigWarn("Failed to parse heal pool particle \"" + particleName + "\". ", "Main");
        }
      }
    }

    MainConfig.remove_empty_buckets = config.getBoolean("Empty-Buckets", true);
    MainConfig.remove_empty_potions = config.getBoolean("Empty-Potions", true);

    MainConfig.prestiges_level_on_exp_bar = config.getBoolean("Prestiges-Level-On-Experience-Bar", false);

    MainConfig.custom_height_cap_enabled = config.getBoolean("Height-Cap.Enabled", false);
    MainConfig.custom_height_cap_warn = config.getString("Height-Cap.Message", MainConfig.custom_height_cap_warn);
    {
      if (config.contains("Height-Cap.Arenas")) {

        final HashMap<CachedArenaIdentifier, Integer> map = new HashMap<>();

        for (String string : config.getStringList("Height-Cap.Arenas")) {
          if (!string.contains(":"))
            continue;

          final String[] strings = string.split(":");
          final Integer capInt = Helper.get().parseInt(strings[1]);

          if (capInt == null)
            continue;

          map.put(new CachedArenaIdentifier(strings[0]), capInt);
        }

        MainConfig.custom_height_cap_arenas = map;
      }
    }
  }

  public static void loadPermanentEffects(FileConfiguration config, String path) {
    if (config.contains(path)) {

      MainConfig.permanent_effects_arenas.clear();

      for (String string : config.getStringList(path)) {

        if (!string.contains(":"))
          continue;

        final String[] strings = string.split(":");

        final String arenaId = strings[0];
        final String effectName = strings[1];
        Integer amplifier = null;

        if (strings.length > 2)
          amplifier = Helper.get().parseInt(strings[2]);

        if (effectName == null || arenaId == null)
          continue;

        if (amplifier == null)
          amplifier = 1;

        final PotionEffectType type = PotionEffectType.getByName(effectName);

        if (type == null)
          continue;

        final PotionEffect effect = new PotionEffect(type, Integer.MAX_VALUE, amplifier);

        MainConfig.permanent_effects_arenas.put(new CachedArenaIdentifier(arenaId), effect);
      }
    }
  }

  public static void loadOldSwordsTools(FileConfiguration config) {
    SwordsToolsConfig.anti_chest_enabled = config.getBoolean("Anti-Chest.Enabled");
    {
      if (config.contains("Anti-Chest.Materials")) {

        final List<Material> antiChestMaterials = new ArrayList<>();

        for (String materialName : config.getStringList("Anti-Chest.Materials")) {
          final Material material = Helper.get().getMaterialByName(materialName);

          if (material != null)
            antiChestMaterials.add(material);

        }

        SwordsToolsConfig.anti_chest_materials = antiChestMaterials;
      }
    }

    SwordsToolsConfig.anti_drop_enabled = config.getBoolean("Anti-Drop.Enabled");
    {
      if (config.contains("Anti-Drop.Materials")) {

        final List<Material> antiDropMaterials = new ArrayList<>();

        for (String materialName : config.getStringList("Anti-Drop.Materials")) {
          final Material material = Helper.get().getMaterialByName(materialName);

          if (material != null)
            antiDropMaterials.add(material);

        }

        SwordsToolsConfig.anti_drop_materials = antiDropMaterials;
      }
    }

    SwordsToolsConfig.degrading_buygroups_enabled = config.getBoolean("Degrading-BuyGroups.Enabled", false);
    if (config.contains("Degrading-BuyGroups.BuyGroups"))
      SwordsToolsConfig.degrading_buygroups = config.getStringList("Degrading-BuyGroups.BuyGroups");

    SwordsToolsConfig.advanced_tool_replacement_enabled = config.getBoolean("Advanced-Tool-Replacement.Enabled", false);
    SwordsToolsConfig.advanced_tool_replacement_force_ordered = config.getBoolean("Advanced-Tool-Replacement.Force-Ordered", false);
    SwordsToolsConfig.advanced_tool_replacement_force_ordered_problem = config.getString("Advanced-Tool-Replacement.Force-Ordered-Problem", "&cMissing Problem");
    if (config.contains("Advanced-Tool-Replacement.BuyGroups"))
      SwordsToolsConfig.degrading_buygroups = config.getStringList("Advanced-Tool-Replacement.BuyGroups");

    SwordsToolsConfig.replace_sword_on_buy_enabled = config.getBoolean("Replace-Sword-On-Buy.Enabled", false);
    SwordsToolsConfig.replace_sword_on_buy_all_type = config.getBoolean("Replace-Sword-On-Buy.All-Type", false);

    SwordsToolsConfig.always_sword_chest_enabled = config.getBoolean("Always-Sword.Chest-Deposit", false);
    SwordsToolsConfig.always_sword_drop_enabled = config.getBoolean("Always-Sword.On-Drop", false);

    if (config.contains("Do-Not-Effect"))
      SwordsToolsConfig.tools_swords_do_not_effect = config.getStringList("Do-Not-Effect");
  }
}
