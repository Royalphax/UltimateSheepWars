package fr.asynchronous.sheepwars.core;

import java.io.IOException;

import fr.asynchronous.sheepwars.core.exception.ConfigFileNotSet;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;

public class UltimateSheepWarsAPI {

	private UltimateSheepWarsAPI() {
		throw new IllegalStateException("API class");
	}

	public static boolean registerSheep(SheepManager sheepClass) {
		try {
			return SheepManager.registerSheep(sheepClass);
		} catch (ConfigFileNotSet | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean unregisterSheep(SheepManager sheepClass) {
		return SheepManager.unregisterSheep(sheepClass);
	}

	public static boolean registerKit(KitManager kitClass) {
		try {
			return KitManager.registerKit(kitClass);
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
}
