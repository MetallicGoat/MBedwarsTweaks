package me.metallicgoat.tweaksaddon.tweaks.spectialitems;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.specialitem.SpecialItem;
import de.marcely.bedwars.api.game.specialitem.SpecialItemType;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.Pair;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

public class TrackerDistance implements Listener {

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

  private final List<Arena> tickingArenas = new CopyOnWriteArrayList<>();
  private BukkitTask task = null;

  @EventHandler
  public void onRoundStart(RoundStartEvent event) {
    if (!MainConfig.tracker_hotbar_message_enabled)
      return;

    tickingArenas.add(event.getArena());

    if (task == null)
      startTask();

  }

  // Stop checkin' when no ticking arenas
  @EventHandler(priority = EventPriority.MONITOR)
  public void onArenaStatusChange(ArenaStatusChangeEvent event) {
    if (event.getArena().getStatus() == ArenaStatus.RUNNING)
      return;

    tickingArenas.remove(event.getArena());

    if (tickingArenas.isEmpty() && task != null) {
      task.cancel();
      task = null;
    }
  }

  private void startTask() {
    if (task != null)
      task.cancel();

    task = new BukkitRunnable() {
      @Override
      public void run() {
        for (Arena arena : tickingArenas) {

          if (arena.getStatus() != ArenaStatus.RUNNING)
            continue;

          for (Player player : arena.getPlayers()) {
            final boolean holdingTracker = isTracker(player.getItemInHand()) || isTracker(Util.getItemInOffHand(player));

            if (!holdingTracker)
              continue;

            final Team playerTeam = arena.getPlayerTeam(player);
            final Pair<Player, Double> nearestEnemy = getNearestEnemy(arena, player, playerTeam);

            if (nearestEnemy.getKey() == null) {
              // No enemies found message
              sendHotbarMessage(player, Message.build(MainConfig.tracker_hotbar_message_no_enemies));
              return;
            }

            final Team enemyTeam = arena.getPlayerTeam(nearestEnemy.getKey());

            if (enemyTeam == null) {
              throw new RuntimeException("Player Tracker Error: Enemy team is null");
            }

            // Send distance message
            sendHotbarMessage(player, Message.build(MainConfig.tracker_hotbar_message)
                .placeholder("distance", DECIMAL_FORMAT.format(nearestEnemy.getValue()))
                .placeholder("team-color", enemyTeam.getBungeeChatColor())
                .placeholder("team", enemyTeam.getDisplayName(player)));
          }
        }
      }
    }.runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 20L, 20L);
  }

  private void sendHotbarMessage(Player player, Message message) {
    final String string = message.done(player);

    if (string.isEmpty())
      return;

    NMSHelper.get().showActionbar(player, string);
  }

  private Pair<Player, Double> getNearestEnemy(Arena arena, Player player, Team playerTeam) {
    Player nearest = null;
    double nearestDistance = Double.MAX_VALUE;

    for (Player possibleEnemy : arena.getPlayers()) {
      if (playerTeam == arena.getPlayerTeam(possibleEnemy) || possibleEnemy.getGameMode() == GameMode.SPECTATOR)
        continue;

      final double distance = possibleEnemy.getLocation().distance(player.getLocation());

      if (distance < nearestDistance) {
        nearest = possibleEnemy;
        nearestDistance = distance;
      }
    }

    return new Pair<>(nearest, nearestDistance);
  }

  private boolean isTracker(@Nullable ItemStack itemStack) {
    if (itemStack == null)
      return false;

    final SpecialItem item = GameAPI.get().getSpecialItem(itemStack);

    return item != null && item.getType() == SpecialItemType.TRACKER;
  }
}
