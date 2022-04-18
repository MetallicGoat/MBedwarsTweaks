package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.game.spawner.Spawner;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class ConfigValue {

    public static boolean sponge_particles_enabled = false;

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