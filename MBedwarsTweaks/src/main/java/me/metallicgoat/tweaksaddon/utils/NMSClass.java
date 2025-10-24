package me.metallicgoat.tweaksaddon.utils;

import de.marcely.bedwars.tools.NMSHelper;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class NMSClass {

  public static void disableDragonSound() throws Exception {
    final int serverVersion = NMSHelper.get().getVersion();
    final String craftBukkitPackage = Bukkit.getServer().getClass().getPackage().getName();

    final Class<?> craftWorldClass = Class.forName(craftBukkitPackage + ".CraftWorld");
    final Class<?> spigotConfigClass = Class.forName("org.spigotmc.SpigotWorldConfig");
    final Class<?> nmsWorldClass;

    if (serverVersion > 16) {
      nmsWorldClass = Class.forName("net.minecraft.world.level.World");

    } else {
      final String serverString = "v1_" + serverVersion + "_R" + NMSHelper.get().getRevision();

      nmsWorldClass = Class.forName("net.minecraft.server." + serverString + ".World");
    }

    for (World world : Bukkit.getWorlds()) {
      final Object craftWorld = craftWorldClass.cast(world);
      final Object nmsWorld = craftWorldClass.getMethod("getHandle").invoke(craftWorld);
      final Object worldConfig = nmsWorldClass.getField("spigotConfig").get(nmsWorld);

      // set value to 1 to disable (0 is global)
      spigotConfigClass.getField("dragonDeathSoundRadius").set(worldConfig, 1);
    }
  }
}
