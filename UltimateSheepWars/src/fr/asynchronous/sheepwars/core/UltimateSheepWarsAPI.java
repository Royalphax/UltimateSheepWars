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

	public static boolean registerSheep(SheepManager sheepClass) {
		try {
			return SheepManager.registerSheep(sheepClass);
		} catch (ConfigFileNotSet | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean unregisterSheep(SheepManager sheepClass) {
		return SheepManager.unregisterSheep(sheepClass);
	}

	public static boolean registerKit(KitManager kitClass, Plugin owningPlugin) {
		try {
			return KitManager.registerKit(kitClass, owningPlugin);
		} catch (ConfigFileNotSet | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean unregisterKit(KitManager kitClass) {
		return KitManager.unregisterKit(kitClass);
	}

	public static boolean registerBooster(BoosterManager boosterClass) {
		try {
			return BoosterManager.registerBooster(boosterClass);
		} catch (ConfigFileNotSet | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean unregisterBooster(BoosterManager boosterClass) {
		return BoosterManager.unregisterBooster(boosterClass);
	}
	
	public static void setKitsInventory(Class<? extends GuiScreen> kitsInventory) {
		GuiManager.setKitsInventory(kitsInventory);
	}
	
	public static void registerKits(Plugin owningPlugin, KitManager... classes) {
		for (KitManager clazz : classes) 
			registerKit(clazz, owningPlugin);
	}
	
	public static void registerBoosters(BoosterManager... classes) {
		for (BoosterManager clazz : classes) 
			registerBooster(clazz);
	}
	
	public static void registerSheeps(SheepManager... classes) {
		for (SheepManager clazz : classes) 
			registerSheep(clazz);
	}
	
	public static void unregisterKits(KitManager... classes) {
		for (KitManager clazz : classes) 
			unregisterKit(clazz);
	}
	
	public static void unregisterBoosters(BoosterManager... classes) {
		for (BoosterManager clazz : classes) 
			unregisterBooster(clazz);
	}
	
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
}
