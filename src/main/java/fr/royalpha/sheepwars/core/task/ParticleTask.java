package fr.royalpha.sheepwars.core.task;

import java.util.HashMap;

import fr.royalpha.sheepwars.core.handler.Contributor;
import fr.royalpha.sheepwars.core.handler.Particles;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;

public class ParticleTask extends BukkitRunnable {

	private static HashMap<Player, ParticleTask> map = new HashMap<>();

	private Particles.ParticleEffect effect;
	private Player player;
	private boolean isMoving;
	public ParticleTask(Particles.ParticleEffect effect, Player player, SheepWarsPlugin plugin) {
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
			if (this.player.getGameMode() != GameMode.SPECTATOR) {
				if (Contributor.isContributor(player)) {
					Contributor contrib = Contributor.getContributor(player);
					if (this.effect == contrib.getEffect() && contrib.isEffectActive())
						this.effect.getAction().update(this.player, this.isMoving);
				} else {
					this.effect.getAction().update(this.player, this.isMoving);
				}
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
