package me.metallicgoat.tweaksaddon.tweaks.spawners;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.player.PlayerPickupDropEvent;
import de.marcely.bedwars.api.event.player.PlayerQuitArenaEvent;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class AFKSpawners implements Listener {

	private final HashMap<Player, Long> moveTimes = new HashMap<>();

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!MainConfig.afk_spawners_enabled)
			return;

		final Arena arena = GameAPI.get().getArenaByPlayer(event.getPlayer());

		if (arena == null || arena.getStatus() != ArenaStatus.RUNNING)
			return;

		moveTimes.put(event.getPlayer(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitArenaEvent event) {
		if (!MainConfig.afk_spawners_enabled)
			return;

		moveTimes.remove(event.getPlayer());
	}

	@EventHandler
	public void onSpawnerPickup(PlayerPickupDropEvent event) {
		if (!MainConfig.afk_spawners_enabled || event.isCancelled())
			return;

		final Player player = event.getPlayer();

		if (!event.isFromSpawner() || !moveTimes.containsKey(player))
			return;

		final long secsSinceLastMove = (System.currentTimeMillis() - moveTimes.get(player)) / 1000;

		if (secsSinceLastMove > MainConfig.afk_spawners_time)
			event.setCancelled(true);
	}
}