package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.VarParticle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import me.metallicgoat.tweaksaddon.config.ConfigManager.Config;
import me.metallicgoat.tweaksaddon.config.ConfigManager.SectionTitle;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MainConfig {

  @Getter
  private static final List<Material> defaultMaterials = new ArrayList<>(Arrays.asList(
      Helper.get().getMaterialByName("WOODEN_SWORD"),
      Helper.get().getMaterialByName("SHEARS"),
      Helper.get().getMaterialByName("WOODEN_PICKAXE"),
      Helper.get().getMaterialByName("STONE_PICKAXE"),
      Helper.get().getMaterialByName("IRON_PICKAXE"),
      Helper.get().getMaterialByName("GOLDEN_PICKAXE"),
      Helper.get().getMaterialByName("DIAMOND_PICKAXE"),
      Helper.get().getMaterialByName("WOODEN_AXE"),
      Helper.get().getMaterialByName("STONE_AXE"),
      Helper.get().getMaterialByName("IRON_AXE"),
      Helper.get().getMaterialByName("GOLDEN_AXE"),
      Helper.get().getMaterialByName("DIAMOND_AXE")
  ));

  // ===== GENERAL
  @SectionTitle(title = "GENERAL")

  @Config(
      description = {
          "If enabled, we will checks for a Tweaks update when the plugin is loaded."
      }
  )
  public static boolean check_update_on_load = true;

  // ===== GEN TIERS
  @SectionTitle(title = "GEN TIERS")

  @Config(
      description = {
          "Tiers can be configured in the gen-tiers.yml"
      }
  )
  public static boolean gen_tiers_enabled = false;

  @Config(
      description = {
          "Set custom holo tiles for spawners with Gen Tiers",
          "Placeholders: {spawner} {spawner-color} {time} {tier}"
      }
  )
  public static boolean gen_tiers_custom_holo_enabled = false;
  @Config public static List<String> gen_tiers_custom_holo_titles = new ArrayList<>(Arrays.asList(
      "{spawner-color}{spawner}",
      "{tier}",
      "&eSpawning in &c{time} &eseconds!"
  ));

  @Config(
      description = {
          "Adds 'Tier I' to spawners listed",
          "Add the spawner id of the item being dropped"
      }
  )
  public static String gen_tiers_start_tier = "&eTier &cI";
  @Config public static List<DropType> gen_tiers_start_spawners = new ArrayList<>();

  @Config(
      description = {
          "If a default dragon should spawn in when the sudden death tier is reached",
          "The default dragon will blong to no team, and could target any base or player",
          "To allow players to purchase a team dragon, ",
          "add the 'sudden-death' upgrade to your upgrade-shop.yml"
      }
  )
  public static boolean default_sudden_death_dragon_enabled = true;

  @Config(
      description = {
          "Tries to disable the dragon death sound by overriding the value set in the spigot.yml",
          "Disabling this will require a complete server restart (bw reload will not have an effect)"
      }
  )
  public static boolean disable_dragon_death_sound = true;

  @Config(
      description = {
          "The speed of the dragons spawned at sudden death"
      }
  )
  public static double dragon_speed = 0.8;

  // ===== SPAWNERS
  @SectionTitle(title = "SPAWNERS")

  @Config(
      description = {
          "Disable generators in empty bases",
          "Range = distance from team spawn to spawner"
      }
  )
  public static boolean disable_empty_generators = false;
  @Config public static double disable_empty_generators_range = 6;
  @Config public static List<DropType> disable_empty_generators_spawners = new ArrayList<>();

  @Config(
      description = {
          "Adds the ability to setup Emerald forge (or something custom)"
      }
  )
  public static boolean advanced_forge_enabled = false;
  @Config public static int advanced_forge_range = 20;
  @Config public static int advanced_forge_level = 3;
  @Config public static String advanced_forge_effected_spawner = "iron";
  @Config public static String advanced_forge_new_drop = "emerald";
  @Config public static int advanced_forge_drop_rate = 15;

  @Config(
      description = {
          "Prevents players from picking up spawner drops if they are AFK for a certain amount of time"
      }
  )
  public static boolean afk_spawners_enabled = false;
  @Config public static int afk_spawners_time = 40;

  // ===== PARTICLE EFFECTS
  @SectionTitle(title = "PARTICLE EFFECTS")

  @Config(
      description = {
          "Cool particle effect when you place a sponge"
      }
  )
  public static boolean sponge_particles_enabled = true;

  @Config(
      description = {
          "Spawns green particles around team base when a team purchases Heal Pool"
      }
  )
  public static boolean heal_pool_particle_enabled = true;
  @Config public static boolean heal_pool_particle_team_view_only = true;
  @Config public static int heal_pool_particle_range = 15;
  @Config public static VarParticle heal_pool_particle_type = VarParticle.newInstanceByName(NMSHelper.get().getVersion() > 8 ? "VILLAGER_HAPPY" : "HAPPY_VILLAGER");

  // ===== EXPLOSIVES
  @SectionTitle(title = "EXPLOSIVES")

  @Config(
      description = {
          "Fireball use cool down (20 ticks = 1 sec)"
      }
  )
  public static boolean fireball_cooldown_enabled = true;
  @Config public static long fireball_cooldown_time = 20L;

  @Config(
      description = {
          "Effects given when a fireball is thrown (Default is like hypixel)",
          "Specify which potion effects the player shall gain after throwing a fireball",
          "Usage: <potion effect name>:<duration in ticks (20 ticks = 1 sec):<level>"
      }
  )
  public static boolean fireball_throw_effects_enabled = true;
  @Config public static List<PotionEffect> fireball_throw_effects = Collections.singletonList(
      new PotionEffect(PotionEffectType.SLOW, 25, 0)
  );

  @Config(
      description = {
          "Blocks that fireballs will not destroy (Overrides MBedwars' BlackList)"
      }
  )
  public static boolean fireball_whitelist_enabled = false;
  @Config public static List<Material> fireball_whitelist_blocks = Collections.singletonList(Helper.get().getMaterialByName("END_STONE"));

  @Config(
      description = {
          "Allows you to increase, or decrease the amount of fall damage you receive due to explosions",
          "IMPORTANT: This will be applied on top of the MBedwars 'fall-damage-multiplier' config"
      }
  )
  public static double tnt_fall_damage_multiplier = 1;
  @Config public static double fireball_fall_damage_multiplier = 1;

  // ===== MESSAGES
  @SectionTitle(title = "MESSAGES")

  @Config(
      description = {
          "Message displayed when any team is eliminated (to everyone in the arena)",
          "Placeholders: {team-color} {team-name}"
      }
  )
  public static boolean team_eliminate_message_enabled = true;
  @Config public static List<String> team_eliminate_message = new ArrayList<>(Arrays.asList(
      " ",
      "&f&lTEAM ELIMINATED > {team-color}{team-name} Team &chas been eliminated!",
      " "
  ));

  @Config(
      description = {
          "Add a suffix to the end of a message if the kill is final"
      }
  )
  public static boolean final_kill_suffix_enabled = true;
  @Config public static String final_kill_suffix = " &b&lFINAL KILL!";

  @Config(
      description = {
          "Message sent to players when they purchase an item",
          "Placeholders: {amount} {product}"
      }
  )
  public static boolean buy_message_enabled = true;
  @Config public static String buy_message = "&aYou Purchased &6{product} x{amount}";

  @Config(
      description = {
          "Top killer message displayed at the end of a round"
      }
  )
  public static boolean top_killer_message_enabled = true;
  @Config public static List<String> top_killer_pre_lines = new ArrayList<>(Arrays.asList(
      "&a&l-------------------------------",
      "                &lBedWars",
      " "
  ));
  @Config public static HashMap<Integer, String> top_killer_lines = new HashMap<Integer, String>() {{
    put(1, "    &e&l1st Killer &7- {killer-name} - {kill-amount}");
    put(2, "    &6&l2nd Killer &7- {killer-name} - {kill-amount}");
    put(3, "    &c&l3rd Killer &7- {killer-name} - {kill-amount}");
  }};
  @Config public static List<String> top_killer_sub_lines = new ArrayList<>(Arrays.asList(
      " ",
      "&a&l-------------------------------"
  ));

  @Config(
      description = {
          "Displayed if Top-Killer-Message IS enabled, but there are no top killers"
      }
  )
  public static boolean no_top_killer_message_enabled = false;
  @Config public static List<String> no_top_killer_message = new ArrayList<>(Arrays.asList(
      " ",
      "&eNo Top Killers This Round",
      " "
  ));

  @Config(
      description = {
          "Message displayed when any bed is broken (to everyone in the arena)"
      }
  )
  public static boolean custom_bed_break_message_enabled = false;
  @Config public static List<String> custom_bed_break_message = new ArrayList<>(Arrays.asList(
      " ",
      "&f&lBED DESTRUCTION > {team-color}{team-name} Bed &7was destroyed by {destroyer-color}{destroyer-name}",
      " "
  ));

  @Config(
      description = {
          "Message displayed when all beds are broken by the gen tiers system"
      }
  )
  public static boolean auto_bed_break_message_enabled = false;
  @Config public static List<String> auto_bed_break_message = new ArrayList<>(Arrays.asList(
      " ",
      "&c&lALL BEDS HAVE BEEN DESTROYED",
      " "
  ));

  @Config(
      description = {
          "Titles displayed when a bed is broken. Overrides the MBedwars titles"
      }
  )
  public static boolean bed_destroy_title_enabled = false;
  @Config public static String bed_destroy_title = "&cBED DESTROYED";
  @Config public static String bed_destroy_subtitle = "&fYou will no longer respawn!";

  @Config(
      description = {
          "You may want to disable the MBedwars team name actionbar in the config.yml"
      }
  )
  public static boolean custom_action_bar_in_lobby = false;
  @Config public static boolean custom_action_bar_in_game = false;
  @Config public static String custom_action_bar_message = "%tweaks_next-tier%";

  // ===== CHESTS
  @SectionTitle(title = "CHESTS")

  @Config(
      description = {
          "Prevents player from opening a bases' chest if bases team is still alive"
      }
  )
  public static boolean lock_team_chest_enabled = true;
  @Config public static double lock_team_chest_range = 8;
  @Config public static String lock_team_chest_fail_open = "&cYou cannot open this chest until {team} &chas been eliminated.";

  // MISCELLANEOUS
  @SectionTitle(title = "MISCELLANEOUS")

  @Config(
      description = {
          "Remove items on use"
      }
  )
  public static boolean remove_empty_buckets = true;
  @Config public static boolean remove_empty_potions = true;

  @Config(
      description = {
          "Allows players to place blocks on a bed WITHOUT crouching"
      }
  )
  public static boolean allow_block_place_on_bed = false;

  @Config(
      description = {
          "If enabled you still join a server even if the Player Limit is reached"
      }
  )
  public static boolean player_limit_bypass = false;

  @Config(
      description = {
          "Blocks stats from changing in certain arenas"
      }
  )
  public static boolean block_stat_change_enabled = false;
  @Config public static List<String> block_stat_change_arenas = new ArrayList<>();

  @Config(
      description = {
          "Change default team chat colors",
          "Add teams like: \"DefaultTeamName: ChatColorName\"",
      }
  )
  public static boolean custom_team_colors_enabled = false;
  @Config public static HashMap<Team, ChatColor> custom_team_colors = new HashMap<Team, ChatColor>() {{
    put(Team.CYAN, ChatColor.DARK_AQUA);
  }};

  @Config(
      description = {
          "Permanent effects players have while playing",
          "ArenaName: PotionEffectName:time:amplifier",
          "Supports arena conditions"
      }
  )
  public static boolean permanent_effects_enabled = false;
  @Config public static HashMap<String, PotionEffect> permanent_effects_arenas = new HashMap<String, PotionEffect>() {{
    put("Ruins", new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
  }};

  @Config(
      description = {
          "Removes invisibility on specified DamageCause. See the full list of DamageCauses here:",
          "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html"
      }
  )
  public static boolean remove_invis_ondamage_enabled = true;
  @Config public static List<EntityDamageEvent.DamageCause> remove_invis_damge_causes = new ArrayList<>(Arrays.asList(
      EntityDamageEvent.DamageCause.ENTITY_ATTACK,
      EntityDamageEvent.DamageCause.PROJECTILE,
      EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
      EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
  ));

  @Config(
      description = {
          "If this is enabled, MBedwars dealers/upgrade-dealers will look at the closest players,",
          "rather than stay in one position. Their position will reset when the round ends",
          "Range is how close players must be for Friendly Villagers to activate",
          "If Check-Visibility is enabled, the npc will only look at a player if it has direct line of sight"
      }
  )
  public static boolean friendly_villagers_enabled = true;
  @Config public static int friendly_villagers_range = 20;
  @Config public static boolean friendly_villagers_check_visibility = true;

  // ===== PLACEHOLDER API
  @SectionTitle(title = "PLACEHOLDER API")

  @Config(
      description = {
          "PAPI Placeholder: %tweaks_next-tier%",
          "Placeholders: {next-tier} {time} {sec} {min}"
      }
  )
  public static String papi_next_tier_lobby_starting = "&fStarting in &a{time}s";
  @Config public static String papi_next_tier_lobby_waiting = "&fWaiting...";
  @Config public static String papi_next_tier_lobby_end_lobby = "&rGame Over";
  @Config public static String papi_next_tier_lobby_stopped = "&rArena Stopped";
  @Config public static String papi_next_tier_lobby_resetting = "&rArena Regenerating";
  @Config public static String papi_next_tier_lobby_running = "&f{next-tier} in &a{time}";

  @Config(
      description = {
          "PAPI Placeholder: %tweaks_team-status-{name}%",
          "To be used on the scoreboard as is above"
      }
  )
  public static String papi_team_status_has_bed = "&a✔";
  @Config public static String papi_team_status_team_dead = "&c✘";
  @Config public static String papi_team_status_no_bed = "&a{player-amount}";
  @Config public static String papi_team_status_your_team_suffix = " &7You";

  @Config(
      description = {
          "Whether to count spectators for the player count placeholders"
      }
  )
  public static boolean papi_count_spectators_as_players = false;

  @Config(
      description = {
          "PAPI-Placeholder: %tweaks_team-you-{name}%",
          "Displays specified value if the team in the placeholder matches the players current team"
      })
  public static String papi_team_you_placeholder = " &7You";

  @Config(
      description = {
          "PAPI Placeholder: %tweaks_arena-{mode-name}%",
          "ArenaCondition:ModeName"
      }
  )
  public static HashMap<String, String> papi_arena_mode = new HashMap<String, String>() {{
    put("[players_per_team=1]", "Solos");
    put("[players_per_team=2]", "Doubles");
    put("[players_per_team=3]", "Trios");
    put("[players_per_team=4]", "Quads");
  }};

  @Config(
      description = {
          "PAPI Placeholder: %tweaks_player-arena-running-time%",
          "Displays how long an arena has been running for"
      }
  )
  public static String papi_player_arena_running_time = "{min}:{sec}";

  @Config(
      description = {
          "If set to true, the scoreboard will be force updated to refresh PAPI placeholders"
      }
  )
  public static boolean scoreboard_updating_enabled_in_game = true;
  @Config public static boolean scoreboard_updating_enabled_in_lobby = true;

  // ===== HOOKS
  @SectionTitle(title = "HOOKS")

  @Config(
      description = {
          "REQUIRED DEPENDENCY: PrestigesAddon (By WhoHarsh)",
          "Sets your prestiges bedwars level on experience bar during a game"
      }
  )
  public static boolean prestiges_level_on_exp_bar = false;

  // ===== UNSUPPORTED
  @SectionTitle(title = "UNSUPPORTED")

  @Config(
      description = {
          "THIS WILL BE REMOVED IN THE FUTURE - USE MBedwars ENDER CHESTS",
          "Personal Ender Chests. Overrides MBedwars Team Ender Chests",
          "This config now simply enables the setting in MBedwars by modifying the config value"
      }
  )
  public static boolean personal_ender_chests_enabled = false;

  @Config(
      description = {
          "Add a height cap for specific MBedwars arenas",
          "Add height cap like 'arenaName: 70' (supports arena conditions)"
      }
  )
  public static boolean custom_height_cap_enabled = false;
  @Config public static String custom_height_cap_warn = "&cYou cannot build any higher";
  @Config public static HashMap<String, Integer> custom_height_cap_arenas = new HashMap<String, Integer>() {{
    put("ArenaName", 70);
  }};
}