package fr.asynchronous.sheepwars.core.task;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.Contributor.ParticleEffect;

public class ParticleTask extends BukkitRunnable {

	private static HashMap<Player, ParticleTask> map = new HashMap<>();

	private ParticleEffect effect;
	private Player player;
	private boolean isMoving;
	public ParticleTask(ParticleEffect effect, Player player, UltimateSheepWarsPlugin plugin) {
		if (!map.containsKey(player)) {
			this.isMoving = false;
			this.effect = effect;
			this.player = player;
			map.put(player, this);
			this.runTaskTimer(plugin, 0, effect.getTicks());
		}
	}

	public void run() {
		if (this.player.isOnline()) {
			if (Contributor.isContributor(player) && Contributor.getContributor(player).isEffectActive() && this.player.getGameMode() != GameMode.SPECTATOR) {
				this.effect.getAction().update(this.player, this.isMoving);
			}
		} else {
			this.cancel();
		}
		this.isMoving = false;
	}

	public static void stop(Player player) {
		if (map.containsKey(player))
			map.get(player).cancel();
	}

	public static void move(Player player) {
		if (map.containsKey(player))
			map.get(player).isMoving = true;
	}
}
