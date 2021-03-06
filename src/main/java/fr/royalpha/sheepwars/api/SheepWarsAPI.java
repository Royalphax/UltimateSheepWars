package fr.royalpha.sheepwars.api;

import java.io.IOException;

import fr.royalpha.sheepwars.core.exception.ConfigFileNotSet;
import fr.royalpha.sheepwars.core.gui.GuiManager;
import fr.royalpha.sheepwars.api.gui.base.GuiScreen;
import org.bukkit.plugin.Plugin;

/**
 * This wonderful class allows you to fully take control on the plugin UltimateSheepWars.
 * 
 * @author Roytreo28
 */
public class SheepWarsAPI {

	/**
	 * The Sheepwars Sheep Name is an instance of the class Message.
	 */
	public static final String SHEEPWARS_SHEEP_ID_METADATA = "sheepwars_sheep_id";
	public static final String SHEEPWARS_SHEEP_LAUNCHER_METADATA = "sheepwars_sheep_launcher";
	public static final String SHEEPWARS_API_PREFIX = "[UltimateSheepWars > API] ";

	private SheepWarsAPI() {
		throw new IllegalStateException("API class");
	}

	/**
	 * Register your custom sheep.
	 * 
	 * @param sheepClass
	 *            Instance of your sheep class.
	 * @return true if no error happens.
	 * @exception ConfigFileNotSet
	 *                IOException
	 */
	public static boolean registerSheep(SheepWarsSheep sheepClass) {
		try {
			return SheepWarsSheep.registerSheep(sheepClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unregister your custom sheep.
	 * 
	 * @param sheepClass
	 *            Instance of your sheep class.
	 * @return true if your sheep was unregistered.
	 */
	public static boolean unregisterSheep(SheepWarsSheep sheepClass) {
		return SheepWarsSheep.unregisterSheep(sheepClass);
	}

	/**
	 * Unregister all registred sheeps.
	 * 
	 * @return number of sheep classes that have been erased.
	 */
	public static int unregisterAllSheeps() {
		int i = 0;
		for (SheepWarsSheep sheep : SheepWarsSheep.getAvailableSheeps()) {
			unregisterSheep(sheep);
			i++;
		}
		return i;
	}

	/**
	 * Register your custom kit.
	 * 
	 * @param kitClass
	 *            Instance of your kit class.
	 * @param owningPlugin
	 *            Custom kit plugin instance (your plugin instance).
	 * @return true if no error happens.
	 * @exception ConfigFileNotSet
	 *                IOException
	 */
	public static boolean registerKit(SheepWarsKit kitClass, Plugin owningPlugin) {
		try {
			return SheepWarsKit.registerKit(kitClass, owningPlugin);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unregister your custom kit.
	 * 
	 * @param kitClass
	 *            Instance of your kit class.
	 * @return true if your kit was unregistered.
	 */
	public static boolean unregisterKit(SheepWarsKit kitClass) {
		return SheepWarsKit.unregisterKit(kitClass);
	}

	/**
	 * Unregister all registred kits.
	 * 
	 * @return number of kit classes that have been erased.
	 */
	public static int unregisterAllKits() {
		int i = 0;
		for (SheepWarsKit kit : SheepWarsKit.getAvailableKits()) {
			unregisterKit(kit);
			i++;
		}
		return i;
	}

	/**
	 * Register your custom booster.
	 * 
	 * @param boosterClass
	 *            Instance of your booster class.
	 * @return true if no error happens.
	 * @exception ConfigFileNotSet
	 *                IOException
	 */
	public static boolean registerBooster(SheepWarsBooster boosterClass) {
		try {
			return SheepWarsBooster.registerBooster(boosterClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unregister your custom booster.
	 * 
	 * @param boosterClass
	 *            Instance of your booster class.
	 * @return true if your booster was unregistered.
	 */
	public static boolean unregisterBooster(SheepWarsBooster boosterClass) {
		return SheepWarsBooster.unregisterBooster(boosterClass);
	}

	/**
	 * Unregister all registred boosters.
	 * 
	 * @return number of booster classes that have been erased.
	 */
	public static int unregisterAllBoosters() {
		int i = 0;
		for (SheepWarsBooster booster : SheepWarsBooster.getAvailableBoosters()) {
			unregisterBooster(booster);
			i++;
		}
		return i;
	}

	/**
	 * Set your own inventory class to display kits.
	 * 
	 * @param kitsInventory
	 *            Inventory class.
	 */
	public static void setKitsInventory(Class<? extends GuiScreen> kitsInventory) {
		GuiManager.setKitsInventory(kitsInventory);
	}

	/**
	 * Register several kits.
	 */
	public static void registerKits(Plugin owningPlugin, SheepWarsKit... classes) {
		for (SheepWarsKit clazz : classes)
			registerKit(clazz, owningPlugin);
	}

	/**
	 * Register several boosters.
	 */
	public static void registerBoosters(SheepWarsBooster... classes) {
		for (SheepWarsBooster clazz : classes)
			registerBooster(clazz);
	}

	/**
	 * Register several sheeps.
	 */
	public static void registerSheeps(SheepWarsSheep... classes) {
		for (SheepWarsSheep clazz : classes)
			registerSheep(clazz);
	}

	/**
	 * Unregister several kits.
	 */
	public static void unregisterKits(SheepWarsKit... classes) {
		for (SheepWarsKit clazz : classes)
			unregisterKit(clazz);
	}

	/**
	 * Unregister several boosters.
	 */
	public static void unregisterBoosters(SheepWarsBooster... classes) {
		for (SheepWarsBooster clazz : classes)
			unregisterBooster(clazz);
	}

	/**
	 * Unregister several sheeps.
	 */
	public static void unregisterSheeps(SheepWarsSheep... classes) {
		for (SheepWarsSheep clazz : classes)
			unregisterSheep(clazz);
	}

	public static void registerCalendarEvent(Plugin owningPlugin, CalendarEvent calendarEvent) {
		CalendarEvent.registerCalendarEvent(calendarEvent, owningPlugin);
	}

	public static void registerCalendarEvents(Plugin owningPlugin, CalendarEvent... calendarEvents) {
		for (CalendarEvent clazz : calendarEvents)
			registerCalendarEvent(owningPlugin, clazz);
	}

	public static void unregisterCalendarEvent(CalendarEvent calendarEvent) {
		CalendarEvent.unregisterCalendarEvent(calendarEvent);
	}

	public static void unregisterCalendarEvents(CalendarEvent... calendarEvents) {
		for (CalendarEvent clazz : calendarEvents)
			unregisterCalendarEvent(clazz);
	}
}
