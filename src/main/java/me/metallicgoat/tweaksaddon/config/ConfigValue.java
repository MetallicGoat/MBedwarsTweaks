package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.game.spawner.Spawner;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ConfigValue {

    public static boolean sponge_particles_enabled = false;

    public static boolean prevent_liquid_build_up = false;

    public static boolean player_limit_bypass = false;

    public static boolean custom_team_colors_enabled = false;
    public static HashMap<Team, ChatColor> custom_team_colors = new HashMap<>();

    public static boolean permanent_effects_enabled = false;
    public static HashMap<Arena, Collection<PotionEffect>> permanent_effects_arenas = new HashMap<>();

    public static boolean lock_team_chest_enabled = false;
    public static double lock_team_chest_range = 8;
    public static String lock_team_chest_fail_open = "&cYou cannot open this chest until {team} &chas been eliminated.";

    public static boolean personal_ender_chests_enabled = false;
    public static String personal_ender_chests_name = "Ender Chest";

    public static boolean team_eliminate_message_enabled = false;
    public static List<String> team_eliminate_message = new ArrayList<>();

    public static boolean final_kill_suffix_enabled = false;
    public static String final_kill_suffix = " &b&lFINAL KILL!";

    public static boolean buy_message_enabled = false;
    public static String buy_message = "&aYou Purchased &6{product} x{amount}";

    public static boolean remove_invis_ondamage_enabled = false;
    public static List<EntityDamageEvent.DamageCause> remove_invis_remove_causes = new ArrayList<>();

    // Generators
    public static boolean disable_empty_generators = false;
    public static double disable_empty_generators_range = 6;
    public static List<Spawner> disable_empty_generators_spawners = new ArrayList<>();

    // Messages

}