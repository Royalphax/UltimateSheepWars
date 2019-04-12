package fr.asynchronous.sheepwars.core.task;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.booster.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.data.PlayerData;

public class BoosterDisplayTask extends BukkitRunnable {

	private final SheepWarsBooster booster;
	private UUID id;
	private int duration;

	public BoosterDisplayTask(SheepWarsBooster booster, Player activator, Plugin plugin) {
		this.booster = booster;
		this.duration = booster.getDuration() * 20;
		this.booster.onStart(activator, PlayerData.getPlayerData(activator).getTeam());
		if (booster.getDuration() < 1)
			return;
		Bukkit.getPluginManager().registerEvents(this.booster, plugin);
		this.id = SheepWarsPlugin.getVersionManager().getBoosterDisplayer().startDisplay(this.booster);
		this.runTaskTimer(plugin, 0, 0);
	}

	@Override
	public void run() {
		SheepWarsPlugin.getVersionManager().getBoosterDisplayer().tickDisplay(this.id, this.duration, (this.booster.getDuration() * 20));
		this.duration--;
		if (this.duration <= 0) {
			this.cancel();
			SheepWarsPlugin.getVersionManager().getBoosterDisplayer().endDisplay(this.id);
			this.booster.onFinish();
			HandlerList.unregisterAll(this.booster);
		}
	}
}