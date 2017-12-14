package fr.asynchronous.sheepwars.core.gui.manager;

import java.util.HashMap;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.gui.task.GuiTask;

public class GuiManager {

	public static HashMap<String, Class<?>> openGuis = new HashMap<>();

	private GuiManager() {
		throw new IllegalStateException("GuiManager.class hasn't to be instantiated.");
	}
	
	public static GuiScreen openGui(UltimateSheepWarsPlugin plugin, GuiScreen gui) {
		openPlayer(gui.getPlayer(), gui.getClass());
		if (gui.isUpdate())
			new GuiTask(plugin, gui.getPlayer(), gui).runTaskTimer(plugin, 0, 20);
		else {
			gui.open(true);
		}
		return gui;
	}

	@SuppressWarnings("rawtypes")
	private static void openPlayer(Player p, Class gui) {
		if (openGuis.containsKey(p.getName())) {
			openGuis.remove(p.getName());
			openGuis.put(p.getName(), gui);
		} else {
			openGuis.put(p.getName(), gui);
		}
	}

	public static void closePlayer(Player p) {
		if (openGuis.containsKey(p.getName())) {
			openGuis.remove(p.getName());
		}
	}

	public static boolean isPlayer(Player p) {
		if (openGuis.containsKey(p.getName()))
			return true;
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isOpened(Class clas) {
		for (Class cla : openGuis.values()) {
			if (cla.equals(clas))
				return true;
		}
		return false;
	}
}