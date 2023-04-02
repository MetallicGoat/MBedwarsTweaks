package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.VarParticle;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import me.metallicgoat.tweaksaddon.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class ConfigManager {

  @SuppressWarnings("deprecation")
  public static void load(JavaPlugin plugin, Class<?> configValueClass, FileType fileType) {
    final File configFile = new File(MBedwarsTweaksPlugin.getAddon().getDataFolder(), fileType.getFileName());
    final String pluginVer = plugin.getDescription().getVersion();

    if (!configFile.exists()) {
      save(configValueClass, pluginVer, configFile);
      return;
    }

    // load it
    final FileConfiguration config = new YamlConfiguration();

    try {
      config.load(configFile);
    } catch (Exception e) {
      e.printStackTrace();
    }

    final String configVersion = config.getString("version");

    // AUTO UPDATE FROM OLD FORMAT
    if (configVersion == null) {
      switch (fileType) {
        case MAIN:
          ConfigLegacyMigrator.loadOldMain(config);
        case SWORDS_TOOLS:
          ConfigLegacyMigrator.loadOldSwordsTools(config);
      }

      // re-save to new format
      save(configValueClass, pluginVer, configFile);
      return;
    }

    // LOAD FILE
    for (Field field : configValueClass.getDeclaredFields()) {
      if (!field.isAnnotationPresent(Config.class))
        continue;

      final Config configAnno = field.getAnnotation(Config.class);
      final String configName = convertFieldToConfig(field.getName());

      Object configValue = null;
      String configString = null;

      // Try to find the config, if we fail, try to use an old name (from annotation)
      if (config.contains(configName)) {
        configValue = config.get(configName);
        configString = config.getString(configName);

      } else {
        for (String oldName : configAnno.oldNames()) {
          oldName = convertFieldToConfig(oldName);

          if (config.contains(oldName)) {
            configValue = config.get(oldName);
            configString = config.getString(oldName);
            break;
          }
        }
      }

      if (configValue == null) {
        Console.printConfigWarn("You config seems to be missing the config called '" + configName + "'. Have a look in your config.yml", "Main");
        continue;
      }

      try {
        // Load it
        final Object deserialized = deserializeObject(field, field.getType(), configString, configValue);

        if (deserialized == null) {
          Console.printConfigWarn("There seems to be an issue with the config \"" + configName + "\". Have a look in your config.yml", "Main");
          continue;
        }

        // Set it
        field.set(configValueClass, deserialized);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (!configVersion.equals(pluginVer))
      save(configValueClass, pluginVer, configFile);

  }


  public static void save(Class<?> configValueClass, String version, File configFile) {
    final YamlConfigurationDescriptor config = new YamlConfigurationDescriptor();

    config.addComment("Used for Auto Updating. Do not Change! (Unless you know what you are doing)");
    config.set("version", version);
    config.addEmptyLine();

    for (Field field : configValueClass.getDeclaredFields()) {
      if (!field.isAnnotationPresent(Config.class))
        continue;

      if (field.isAnnotationPresent(SectionTitle.class)) {
        final String sectionName = field.getAnnotation(SectionTitle.class).title();

        config.addEmptyLine();
        config.addEmptyLine();
        config.addComment("========== " + sectionName + " ==========");
      }

      final Config configAnno = field.getAnnotation(Config.class);
      final String configName = convertFieldToConfig(field.getName());

      // Add empty line above if we are adding a comment for this config
      if (configAnno.description().length > 0)
        config.addEmptyLine();

      for (String string : configAnno.description())
        config.addComment(string);

      field.setAccessible(true);

      try {
        final Object fieldObject = field.get(configValueClass);

        if (fieldObject == null)
          throw new NullPointerException("Config field is null '" + field.getName() + "' THIS IS A BUG, please report it to us.");

        config.set(configName, serializeObject(field, field.getType(), fieldObject));

      } catch (Exception e) {
        Console.printConfigWarn("Failed to save field '" + field.getName() + "'. THIS IS A BUG, please report it to us.", "Main");
        e.printStackTrace();
      }
    }

    try {
      config.save(configFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String convertFieldToConfig(String input) {
    return Arrays.stream(input.split("_"))
        .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
        .collect(Collectors.joining("-"));
  }

  public static Object deserializeObject(Field field, Class<?> type, String stringObject, Object configObject) {
    // Primitives
    if (type == boolean.class || type == Boolean.class)
      return configObject instanceof Boolean ? configObject : null;
    else if (type == int.class || type == Integer.class)
      return Helper.get().parseInt(stringObject);
    else if (type == double.class || type == Double.class)
      return Helper.get().parseDouble(stringObject);
    else if (type == long.class || type == Long.class)
      return Helper.get().parseLong(stringObject);

      // Strings
    else if (type == String.class) {
      return ChatColor.translateAlternateColorCodes('&', stringObject);

      // ItemStack
    } else if (type == ItemStack.class) {
      return Helper.get().parseItemStack(stringObject);

      // Particle
    } else if (type == VarParticle.class) {
      return VarParticle.newInstanceByName(stringObject);

      // ChatColor
    } else if (type == ChatColor.class) {
      try {
        return ChatColor.valueOf(stringObject.toUpperCase());
      } catch (Exception e) {
        Console.printWarn("Failed to parse color \"" + stringObject + "\"");
      }

      // PotionEffect
    } else if (type == PotionEffect.class) {
      final String[] parts = (stringObject).split(":");

      if (parts.length == 3) {
        final Integer time = parts[1].equalsIgnoreCase("INFINITE") ? Integer.valueOf(Integer.MAX_VALUE) : (isInteger(parts[1]) ? Integer.parseInt(parts[1]) : null);

        if (time != null && isInteger(parts[2])) {
          final PotionEffectType effectType = PotionEffectType.getByName(parts[0]);

          if (effectType != null) {
            return new PotionEffect(effectType, time, Integer.parseInt(parts[2]) - 1);
          } else
            Console.printConfigWarn(parts[0] + " isn't a valid effect type", "Main");
        } else
          Console.printConfigWarn(parts[1] + " or " + parts[2] + " isn't a valid number", "Main");
      } else
        Console.printConfigWarn(stringObject + "\" has " + parts.length + " :, but 3 are needed", "Main");

    } else if (type == DropType.class) {
      return GameAPI.get().getDropTypeById(stringObject);

      // Lists + Sets
    } else if (field != null && Collection.class.isAssignableFrom(type)) {
      final List<?> strings = (List<?>) configObject;
      final Class<?> listType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
      final Collection<Object> collection;

      if (type == List.class)
        collection = new ArrayList<>();
      else if (type == Set.class)
        collection = new HashSet<>();
      else
        return null;

      // Any type of Set or List
      for (Object object : strings)
        collection.add(deserializeObject(field, listType, (String) object, object));

      return collection;

      // Map<?, ?>
    } else if (field != null && Map.class.isAssignableFrom(type) && configObject instanceof ConfigurationSection) {
      final Type[] mapTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
      final Class<?> keyType = (Class<?>) mapTypes[0];
      final Class<?> valueType = (Class<?>) mapTypes[1];
      final MemorySection section = (MemorySection) configObject;
      final HashMap<Object, Object> map = new HashMap<>();

      for (Map.Entry<String, Object> entry : section.getValues(false).entrySet())
        map.put(deserializeObject(null, keyType, entry.getKey(), entry.getKey()), deserializeObject(null, valueType, entry.getValue().toString(), entry.getValue()));

      return map;

      // Enums
    } else if (type.isEnum()) {
      for (Object enumVal : type.getEnumConstants()) {
        if (enumVal.toString().equalsIgnoreCase(stringObject)) {
          return enumVal;
        }
      }
    }

    return null;
  }

  // NOTE: Cannot use a switch for classes in java 8, and names won't work because of obfuscation
  // Returning this as an Object just so it looks nicer in yml (for example ints dont need quotes)
  public static @Nullable Object serializeObject(Field field, Class<?> type, Object fieldObject) {
    // Primitives
    if (type == boolean.class || type == Boolean.class ||
        type == int.class || type == Integer.class ||
        type == double.class || type == Double.class ||
        type == float.class || type == Float.class ||
        type == long.class || type == Long.class
    ) {

      return fieldObject;

      // String
    } else if (type == String.class) {
      return replaceChatColorString((String) fieldObject);

      // ItemStack
    } else if (type == ItemStack.class) {
      final ItemStack itemStack = (ItemStack) fieldObject;
      return Helper.get().composeItemStack(itemStack);

      // Particle
    } else if (type == VarParticle.class) {
      final VarParticle varParticle = (VarParticle) fieldObject;
      return varParticle.getName();

      // ChatColor
    } else if (type == ChatColor.class) {
      final ChatColor chatColor = (ChatColor) fieldObject;
      return chatColor.getName();

      // PotionEffect
    } else if (type == PotionEffect.class) {
      final PotionEffect effect = (PotionEffect) fieldObject;
      final int time = effect.getDuration();
      return effect.getType().getName() + ":" + (time == Integer.MAX_VALUE ? "INFINITE" : time) + ":" + (effect.getAmplifier() + 1);

      // DropType
    } else if (type == DropType.class) {
      final DropType dropType = (DropType) fieldObject;
      return dropType.getId();

      // Lists + Sets
    } else if (field != null && Collection.class.isAssignableFrom(type)) {
      final List<String> lines = new ArrayList<>();
      final Collection<?> objectList = (Collection<?>) fieldObject;
      final Class<?> listType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

      // Any type of List or Set
      for (Object object : objectList)
        lines.add((String) serializeObject(null, listType, object));

      return lines;

      // Maps
    } else if (field != null && Map.class.isAssignableFrom(type)) {
      final Map<?, ?> map = (Map<?, ?>) fieldObject;
      final Type[] mapTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
      final Class<?> keyType = (Class<?>) mapTypes[0];
      final Class<?> valueType = (Class<?>) mapTypes[1];
      final ConfigurationSection section = new YamlConfiguration();

      for (Map.Entry<?, ?> entry : map.entrySet())
        section.set(String.valueOf(serializeObject(null, keyType, entry.getKey())), String.valueOf(serializeObject(null, valueType, entry.getValue())));

      return section;

      // Enums
    } else if (type.isEnum()) {
      return fieldObject.toString();
    }

    // Fail
    Console.printConfigWarn("Failed to save config type - " + type.getSimpleName(), "Main");
    return null;
  }

  // Utils
  private static boolean isInteger(String string) {
    try {
      Integer.parseInt(string);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static String replaceChatColorString(String str) {
    final char[] chars = str.toCharArray();

    for (int i = 0; i < chars.length - 1; ++i) {
      if (chars[i] == 167 && "0123456789abcdefklmnor".indexOf(chars[i + 1]) > -1) {
        chars[i] = '&';
      }
    }

    return new String(chars);
  }

  public enum FileType {
    MAIN("config.yml"),
    GEN_TIERS("gen-tiers.yml"),
    SWORDS_TOOLS("swords-tools.yml");

    @Getter private final String fileName;

    FileType(String fileName) {
      this.fileName = fileName;
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface SectionTitle {

    String title();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Config {

    String[] description() default {};

    String[] oldNames() default {};
  }

//  public static String getColorNameOrHex(ChatColor bungeeColor) {
//    String bungeeColorString = bungeeColor.toString();
//
//    // Check if the color is a default Minecraft color
//    for (org.bukkit.ChatColor bukkitColor : org.bukkit.ChatColor.values()) {
//      if (bukkitColor.isColor() && bukkitColor.toString().equals(bungeeColorString)) {
//        return bukkitColor.name();
//      }
//    }
//
//    // If the color is not a default Minecraft color, return its hex color code
//    if (NMSHelper.get().getVersion() >= 16) {
//      try {
//        Field colorField = bungeeColor.getClass().getDeclaredField("color");
//        colorField.setAccessible(true);
//        final int colorValue = (int) colorField.get(bungeeColor);
//
//        return String.format("#%06X", colorValue);
//      } catch (Exception e) {
//        return null;
//      }
//
//    }
//    return null;
//  }
//
//  public static ChatColor getBungeeChatColor(String input) {
//    ChatColor bungeeColor = null;
//
//    try {
//      // Check if input is a Minecraft color name
//      for (org.bukkit.ChatColor bukkitColor : org.bukkit.ChatColor.values()) {
//        if (bukkitColor.isColor() && bukkitColor.name().equalsIgnoreCase(input)) {
//          bungeeColor = ChatColor.getByChar(bukkitColor.getChar());
//          return bungeeColor;
//        }
//      }
//
//      // Check if input is a hex color code and server version is 1.16 or higher
//      if (input.startsWith("#") && NMSHelper.get().getVersion() >= 16) {
//        Method ofMethod = ChatColor.class.getMethod("of", String.class);
//        bungeeColor = (ChatColor) ofMethod.invoke(null, input);
//        return bungeeColor;
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    return bungeeColor;
//  }
}