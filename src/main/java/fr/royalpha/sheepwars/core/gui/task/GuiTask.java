package fr.royalpha.sheepwars.core.gui.task;

import fr.royalpha.sheepwars.core.gui.event.GuiUpdateEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.gui.base.GuiScreen;

public class GuiTask extends BukkitRunnable {

	private final SheepWarsPlugin plugin;
	private final Player player;
	private final GuiScreen gui;

	public GuiTask(SheepWarsPlugin plugin, Player player, String inventoryName, GuiScreen gui) {
		this.plugin = plugin;
		this.player = player;
		this.gui = gui;
		gui.open(plugin, player, inventoryName);
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