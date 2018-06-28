package fr.asynchronous.sheepwars.core.gui.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.gui.event.GuiUpdateEvent;

public class GuiTask extends BukkitRunnable {

	private final UltimateSheepWarsPlugin plugin;
	private final Player player;
	private final GuiScreen gui;

	public GuiTask(UltimateSheepWarsPlugin plugin, Player player, String inventoryName, GuiScreen gui) {
		this.plugin = plugin;
		this.player = player;
		this.gui = gui;
		gui.open(plugin, player, inventoryName, true);
	}

	@Override
	public void run() {

		if (!gui.getInventory().getViewers().contains(this.player)) {
			this.cancel();
			return;
		}

		this.plugin.getServer().getPluginManager().callEvent(new GuiUpdateEvent(this.player, this.gui, false));
		this.gui.drawScreen();
	}
}