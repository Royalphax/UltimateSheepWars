package fr.royalpha.sheepwars.core.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.gui.base.GuiScreen;
import fr.royalpha.sheepwars.core.gui.task.GuiTask;
import fr.royalpha.sheepwars.core.manager.ExceptionManager;
import fr.royalpha.sheepwars.core.message.Message;
import fr.royalpha.sheepwars.core.util.ReflectionUtils;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;

public class GuiManager {

	protected static Map<String, GuiScreen> openGuis = new HashMap<>();
	
	private static Class<? extends GuiScreen> kitsInventoryClass;
	
	private GuiManager() {
		throw new IllegalStateException("GuiManager.class hasn't to be instantiated.");
	}
	
	public static GuiScreen openGui(SheepWarsPlugin plugin, Player player, String inventoryName, GuiScreen gui) {
		openPlayer(player, gui);
		if (gui.isUpdate())
			new GuiTask(plugin, player, inventoryName, gui).runTaskTimer(plugin, 0, 20);
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
			ExceptionManager.register(e, true);
		}
		return null;
	}
	
	public static void openKitsInventory(Player player, SheepWarsPlugin plugin) {
		final PlayerData data = PlayerData.getPlayerData(player);
		String inventoryName = data.getLanguage().getMessage(Message.Messages.KIT_INVENTORY_NAME).replaceAll("%KIT_NAME%", data.getKit().getName(player));
		if (data.getKit().getLevels().size() > 1 && data.getKitLevel() >= 0) {
			inventoryName = inventoryName.replaceAll("%LEVEL_NAME%", data.getKit().getLevel(data.getKitLevel()).getName(data.getLanguage()));
		} else {
			inventoryName = inventoryName.replaceAll("%LEVEL_NAME%", "");
		}
		if (inventoryName.length() > 32)
			inventoryName = inventoryName.substring(0, 32);
		GuiManager.openGui(plugin, player, inventoryName, getKitsInventoryNewInstance());
	}
}