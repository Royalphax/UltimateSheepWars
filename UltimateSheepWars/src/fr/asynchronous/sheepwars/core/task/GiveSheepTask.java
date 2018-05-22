package fr.asynchronous.sheepwars.core.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.manager.SheepManager;

public class GiveSheepTask extends BukkitRunnable {

	public final SheepManager sheepToGive;
	
	public GiveSheepTask(SheepManager sheepToGive) {
		this.sheepToGive = sheepToGive;
	}

	public void run() {
		for (Player online : Bukkit.getOnlinePlayers())
			SheepManager.giveSheep(online, this.sheepToGive);
	}
}
