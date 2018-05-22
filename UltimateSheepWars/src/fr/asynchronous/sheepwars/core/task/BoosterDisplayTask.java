package fr.asynchronous.sheepwars.core.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;

public class BoosterDisplayTask extends BukkitRunnable {

	private final BoosterManager booster;
	private int duration;

	public BoosterDisplayTask(BoosterManager booster, Player activator, Plugin plugin) {
		this.booster = booster;
		this.duration = booster.getDuration() * 20;
		this.booster.onStart(activator, PlayerData.getPlayerData(activator).getTeam());
		if (booster.getDuration() < 1)
			return;
		Bukkit.getPluginManager().registerEvents(this.booster, plugin);
		UltimateSheepWarsPlugin.getVersionManager().getBoosterDisplayer().startDisplay(this.booster);
		this.runTaskTimer(plugin, 0, 0);
	}

	@Override
	public void run() {
		UltimateSheepWarsPlugin.getVersionManager().getBoosterDisplayer().tickDisplay(this.booster, this.duration);
		this.duration--;
		if (this.duration <= 0) {
			this.cancel();
			UltimateSheepWarsPlugin.getVersionManager().getBoosterDisplayer().endDisplay(this.booster);
			this.booster.onFinish();
			HandlerList.unregisterAll(this.booster);
		}
	}
}