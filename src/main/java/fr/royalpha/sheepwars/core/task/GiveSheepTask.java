package fr.royalpha.sheepwars.core.task;

import fr.royalpha.sheepwars.api.SheepWarsSheep;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.kit.DestroyerKit;

public class GiveSheepTask extends BukkitRunnable {

	public final SheepWarsSheep sheepToGive;
	
	public GiveSheepTask(SheepWarsSheep sheepToGive) {
		this.sheepToGive = sheepToGive;
	}

	public void run() {
		for (Player online : Bukkit.getOnlinePlayers()) {
			SheepWarsSheep.giveSheep(online, this.sheepToGive);
			if (PlayerData.getPlayerData(online).getKit().getId() == DestroyerKit.ID)
				online.getInventory().addItem(new ItemStack(Material.TNT, 1));
		}
	}
}
