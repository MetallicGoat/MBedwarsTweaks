package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.Pair;
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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import lombok.Getter;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.utils.Console;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
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
    }

    final boolean isUpdating = configVersion == null || !configVersion.equals(pluginVer);

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
        if (!isUpdating)
          Console.printConfigWarn("Your config seems to be missing the config called " + configName + "'. Have a look in your config.yml", "Main");
        else
          Console.printConfigWarn("New config added during update to " + pluginVer + ": '" + configName + "'", "Main");

        continue;
      }

      try {
        // Load it
        final Object deserialized = deserializeObject(field, field.getGenericType(), configString, configValue, configAnno);

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

    if (isUpdating)
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

        config.set(configName, serializeObject(field.getGenericType(), fieldObject, configAnno));

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

  public static Object deserializeObject(
      Field field,
      Type type,
      String stringObject,
      Object configObject,
      Config configAnno) {

    // Nested generics
    if (type instanceof ParameterizedType) {
      final ParameterizedType paramType = (ParameterizedType) type;
      final Class<?> rawClass = (Class<?>) paramType.getRawType();
      final Type[] typeArgs = paramType.getActualTypeArguments();

      // Lists + Sets
      if (Collection.class.isAssignableFrom(rawClass)) {

        if (!Collection.class.isAssignableFrom(configObject.getClass()))
          return null;

        final Collection<?> strings = (Collection<?>) configObject;
        final Collection<Object> collection;

        if (List.class.isAssignableFrom(rawClass))
          collection = new ArrayList<>();
        else if (Set.class.isAssignableFrom(rawClass)) {
          if (Enum.class.isAssignableFrom((Class<?>) typeArgs[0]))
            collection = EnumSet.noneOf((Class<? extends Enum>) typeArgs[0]);
          else
            collection = new HashSet<>();
        } else
          throw new IllegalStateException("Unable to deserialize " + field.getName());

        // Any type of Set or List
        for (Object rawChild : strings) {
          if (rawChild == null)
            continue;

          final Object deserializedChild = deserializeObject(field, typeArgs[0], rawChild.toString(), rawChild, configAnno);

          if (deserializedChild != null)
            collection.add(deserializedChild);
        }

        return collection;

        // Maps
      } else if (Map.class.isAssignableFrom(rawClass)) {

        // we did it wrong in the past
        if (configObject instanceof List) {
          final Map<String, String> map = new HashMap<>();
          final Collection<String> list = (Collection<String>) configObject;

          for (String entry : list) {
            final String[] parts = entry.split(":");

            if (parts.length >= 2)
              map.put(parts[0], parts[1]);
          }

          return map;
        }

        // proper way...
        if (!MemorySection.class.isAssignableFrom(configObject.getClass()))
          return null;

        final MemorySection section = (MemorySection) configObject;
        Map<Object, Object> map;

        if (ConcurrentMap.class.isAssignableFrom(rawClass))
          map = new ConcurrentHashMap<>();
        else
          map = new HashMap<>();

        for (String key : section.getKeys(false)) {
          final Object desKey = deserializeObject(field, typeArgs[0], key, key, configAnno);

          if (desKey == null)
            continue;

          final Object value = section.get(key);
          final Object desValue = deserializeObject(field, typeArgs[1], value.toString(), value, configAnno);

          if (desValue == null)
            continue;

          map.put(desKey, desValue);
        }

        return map;

        // Pairs
      } else if (rawClass == Pair.class) {

        // No implementation for this
        return null;
      }

      return null;
    }


    // Primitives
    if (type == boolean.class || type == Boolean.class)
      return configObject instanceof Boolean ? configObject : null;
    else if (type == int.class || type == Integer.class)
      return Helper.get().parseInt(stringObject);
    else if (type == double.class || type == Double.class)
      return Helper.get().parseDouble(stringObject);
    else if (type == float.class || type == Float.class)
      return Helper.get().parseDouble(stringObject); // TODO: Helper doesnt have float
    else if (type == long.class || type == Long.class)
      return Helper.get().parseLong(stringObject);

      // Strings
    else if (type == String.class) {
      if (configAnno.autoLowerCase())
        stringObject = stringObject.toLowerCase();
      if (configAnno.formatChatColor())
        stringObject = ChatColor.translateAlternateColorCodes('&', stringObject); // TODO - See MBedwars ColorUtil

      return stringObject;

      // ItemStack
    } else if (type == ItemStack.class) {
      return Helper.get().parseItemStack(stringObject);

      // Material
    } else if (type == Material.class) {
      return Helper.get().getMaterialByName(stringObject);

      // Particle
    } else if (type == VarParticle.class) {
      try {
        return VarParticle.newInstanceByName(stringObject);
      } catch (Exception e) {
        return null;
      }

      // ChatColor
    } else if (type == ChatColor.class) {
      try {
        return ChatColor.valueOf(stringObject.toUpperCase());
      } catch (Exception e) {
        Console.printWarn("Failed to parse color \"" + stringObject + "\"");
      }

      // DropType
    } else if (type == DropType.class) {
      return GameAPI.get().getDropTypeById(stringObject);

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

      // Enums
    } else if (Enum.class.isAssignableFrom((Class<?>) type)) {
      stringObject = stringObject.replaceAll("[ \\-_]", "");

      for (Object enumObj : ((Class<?>) type).getEnumConstants()) {
        String name = ((Enum<?>) enumObj).name();

        name = name.replaceAll("[ \\-_]", "");

        if (name.equalsIgnoreCase(stringObject))
          return enumObj;
      }
    }

    return null;
  }

  // NOTE: Cannot use a switch for classes in java 8, and names won't work because of obfuscation
  // Returning this as an Object just so it looks nicer in yml (for example ints dont need quotes)
  public static @Nullable Object serializeObject(
      Type type,
      Object fieldObject,
      Config configAnno) {

    // Handle nested generics
    if (type instanceof ParameterizedType) {
      final ParameterizedType paramType = (ParameterizedType) type;
      final Class<?> rawClass = (Class<?>) paramType.getRawType();
      final Type[] typeArgs = paramType.getActualTypeArguments();

      // Lists + Sets
      if (Collection.class.isAssignableFrom(rawClass)) {
        final List<Object> serializedList = new ArrayList<>();
        final Collection<?> objectList = (Collection<?>) fieldObject;

        // Any type of List or Set
        for (Object object : objectList) {
          final Object serializedObject = serializeObject(typeArgs[0], object, configAnno);

          if (serializedObject != null) {
            serializedList.add(serializedObject.toString());
          }
        }

        return serializedList;

        // Maps
      } else if (Map.class.isAssignableFrom(rawClass)) {
        final MemorySection section = new MemoryConfiguration();
        final Map<?, ?> map = (Map<?, ?>) fieldObject;

        for (Map.Entry<?, ?> e : map.entrySet()) {
          final Object key = serializeObject(typeArgs[0], e.getKey(), configAnno);
          final Object value = serializeObject(typeArgs[1], e.getValue(), configAnno);

          if (key == null)
            continue;

          section.set(key.toString(), value);
        }

        return section;

        // Pairs
      } else if (rawClass == Pair.class) {

        // No implementation for this
        return null;
      }

      return null;
    }

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
      String str = fieldObject.toString();

      if (configAnno.autoLowerCase())
        str = str.toLowerCase();
      if (configAnno.formatChatColor())
        str = replaceChatColorString(str);

      return str;

      // ItemStack
    } else if (type == ItemStack.class) {
      final ItemStack itemStack = (ItemStack) fieldObject;
      return Helper.get().composeItemStack(itemStack);

      // Material
    } else if (type == Material.class) {
      final Material material = (Material) fieldObject;
      return material.name().toLowerCase();

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

      // Enums
    } else if (Enum.class.isAssignableFrom((Class<?>) type)) {
      return fieldObject.toString();
    }

    // Fail
    Console.printConfigWarn("Failed to save config type - " + ((Class<?>) type).getSimpleName(), "Main");
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

  @Getter
  public enum FileType {
    MAIN("config.yml"),
    GEN_TIERS("gen-tiers.yml"),
    SWORDS_TOOLS("swords-tools.yml");

    private final String fileName;

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

    @Nullable String[] description() default {};

    @Nullable String[] oldNames() default {};

    boolean formatChatColor() default true;

    boolean autoLowerCase() default false;

    boolean priorityLoad() default false;

    boolean appendMetrics() default false;
  }
}