package fr.asynchronous.sheepwars.core.gui.manager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.gui.task.GuiTask;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;

public class GuiManager {

	protected static Map<String, GuiScreen> openGuis = new HashMap<>();
	
	private static Class<? extends GuiScreen> kitsInventoryClass;
	
	private GuiManager() {
		throw new IllegalStateException("GuiManager.class hasn't to be instantiated.");
	}
	
	public static GuiScreen openGui(UltimateSheepWarsPlugin plugin, Player player, String inventoryName, GuiScreen gui) {
		openPlayer(player, gui);
		if (gui.isUpdate())
			new GuiTask(plugin, gui.getPlayer(), inventoryName, gui).runTaskTimer(plugin, 0, 20);
		else {
			gui.open(plugin, player, inventoryName);
		}
		return gui;
	}

	private static void openPlayer(Player p, GuiScreen gui) {
		if (openGuis.containsKey(p.getName()))
			openGuis.remove(p.getName());
		openGuis.put(p.getName(), gui);
	}

	public static void closePlayer(Player p) {
		if (openGuis.containsKey(p.getName()))
			openGuis.remove(p.getName());
	}

	public static boolean hasOpenedGui(Player p) {
		if (openGuis.containsKey(p.getName()))
			return true;
		return false;
	}

	public static boolean isOpened(GuiScreen clas) {
		for (GuiScreen cla : openGuis.values()) {
			if (cla.getId() == clas.getId())
				return true;
		}
		return false;
	}
	
	public static void setKitsInventory(Class<? extends GuiScreen> clazz) {
		kitsInventoryClass = clazz;
	}
	
	public static GuiScreen getKitsInventoryNewInstance() {
		try {
			return (GuiScreen) ReflectionUtils.instantiateObject(kitsInventoryClass);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			new ExceptionManager(e).register(true);
		}
		return null;
	}
}