package fr.asynchronous.sheepwars.core;

import java.io.IOException;

import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.calendar.CalendarEvent;
import fr.asynchronous.sheepwars.core.exception.ConfigFileNotSet;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.gui.manager.GuiManager;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;

/**
 * This wonderful class allows you to fully take control on the plugin UltimateSheepWars.
 * @author Roytreo28
 */
public class UltimateSheepWarsAPI {

	public static final String SHEEPWARS_SHEEP_METADATA = "sheepwars_sheep";
	
	private UltimateSheepWarsAPI() {
		throw new IllegalStateException("API class");
	}

	/**
	 * Register your custom sheep.
	 * 
	 * @param sheepClass Instance of your sheep class.
	 * @return true if no error happens.
	 * @exception ConfigFileNotSet IOException
	 */
	public static boolean registerSheep(SheepManager sheepClass) {
		try {
			return SheepManager.registerSheep(sheepClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unregister your custom sheep.
	 * 
	 * @param sheepClass Instance of your sheep class.
	 * @return true if your sheep was unregistered.
	 */
	public static boolean unregisterSheep(SheepManager sheepClass) {
		return SheepManager.unregisterSheep(sheepClass);
	}

	/**
	 * Register your custom kit.
	 * 
	 * @param kitClass Instance of your kit class.
	 * @param owningPlugin Custom kit plugin instance (your plugin instance).
	 * @return true if no error happens.
	 * @exception ConfigFileNotSet IOException
	 */
	public static boolean registerKit(KitManager kitClass, Plugin owningPlugin) {
		try {
			return KitManager.registerKit(kitClass, owningPlugin);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unregister your custom kit.
	 * 
	 * @param kitClass Instance of your kit class.
	 * @return true if your kit was unregistered.
	 */
	public static boolean unregisterKit(KitManager kitClass) {
		return KitManager.unregisterKit(kitClass);
	}

	/**
	 * Register your custom booster.
	 * 
	 * @param boosterClass Instance of your booster class.
	 * @return true if no error happens.
	 * @exception ConfigFileNotSet IOException
	 */
	public static boolean registerBooster(BoosterManager boosterClass) {
		try {
			return BoosterManager.registerBooster(boosterClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unregister your custom booster.
	 * 
	 * @param boosterClass Instance of your booster class.
	 * @return true if your booster was unregistered.
	 */
	public static boolean unregisterBooster(BoosterManager boosterClass) {
		return BoosterManager.unregisterBooster(boosterClass);
	}
	
	/**
	 * Set your own inventory to display kits.
	 * 
	 * @param kitsInventory Inventory class.
	 */
	public static void setKitsInventory(Class<? extends GuiScreen> kitsInventory) {
		GuiManager.setKitsInventory(kitsInventory);
	}
	
	/**
	 * Register several kits.
	 */
	public static void registerKits(Plugin owningPlugin, KitManager... classes) {
		for (KitManager clazz : classes) 
			registerKit(clazz, owningPlugin);
	}
	
	/**
	 * Register several boosters.
	 */
	public static void registerBoosters(BoosterManager... classes) {
		for (BoosterManager clazz : classes) 
			registerBooster(clazz);
	}
	
	/**
	 * Register several sheeps.
	 */
	public static void registerSheeps(SheepManager... classes) {
		for (SheepManager clazz : classes) 
			registerSheep(clazz);
	}
	
	/**
	 * Unregister several kits.
	 */
	public static void unregisterKits(KitManager... classes) {
		for (KitManager clazz : classes) 
			unregisterKit(clazz);
	}
	
	/**
	 * Unregister several boosters.
	 */
	public static void unregisterBoosters(BoosterManager... classes) {
		for (BoosterManager clazz : classes) 
			unregisterBooster(clazz);
	}
	
	/**
	 * Unregister several sheeps.
	 */
	public static void unregisterSheeps(SheepManager... classes) {
		for (SheepManager clazz : classes) 
			unregisterSheep(clazz);
	}
	
	public static void registerCalendarEvent(Plugin owningPlugin, CalendarEvent calendarEvent) {
		CalendarEvent.enableCalendarEvent(calendarEvent, owningPlugin);
	}
	
	public static void registerCalendarEvents(Plugin owningPlugin, CalendarEvent... calendarEvents) {
		for (CalendarEvent clazz : calendarEvents)
			registerCalendarEvent(owningPlugin, clazz);
	}
	
	public static void unregisterCalendarEvent(CalendarEvent calendarEvent) {
		CalendarEvent.disableCalendarEvent(calendarEvent);
	}
	
	public static void unregisterCalendarEvents(CalendarEvent... calendarEvents) {
		for (CalendarEvent clazz : calendarEvents)
			unregisterCalendarEvent(clazz);
	}
}
