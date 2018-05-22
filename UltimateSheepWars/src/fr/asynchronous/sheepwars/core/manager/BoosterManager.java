package fr.asynchronous.sheepwars.core.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.exception.ConfigFileNotSet;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.task.BoosterDisplayTask;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public abstract class BoosterManager implements Listener {
	private static List<BoosterManager> availableBoosters = new ArrayList<>();
	private static File configFile;
    private static FileConfiguration config;

	private final Message name;
	private final String configPath;
	private DisplayColor displayColor;
	private int duration;

	public BoosterManager(final String name, final DisplayColor displayColor, final int duration) {
		this(new Message(name), name, displayColor, duration);
	}
	
	public BoosterManager(final MsgEnum name, final DisplayColor displayColor, final int duration) {
		this(Message.getMessage(name), name.toString().replaceAll("BOOSTER_", ""), displayColor, duration);
	}
	
	public BoosterManager(final Message name, final String configPath, final DisplayColor displayColor, final int duration) {
		this.name = name;
		this.configPath = "booster." + configPath.replaceAll("_", "-").toLowerCase();
		this.displayColor = displayColor;
		this.duration = duration;
	}
	
	public Message getName() {
		return this.name;
	}

	public DisplayColor getDisplayColor() {
		return this.displayColor;
	}

	public void setDisplayColor(DisplayColor displayColor) {
		this.displayColor = displayColor;
	}

	public int getDuration() {
		return this.duration;
	}

	private String getConfigFieldPath(String field) {
    	return this.configPath + "." + field;
    }


	public abstract boolean onStart(final Player player, final TeamManager team);

	public abstract void onFinish();

	public static BoosterManager activateBooster(Player activator, BoosterManager booster, Plugin plugin) {
		new BoosterDisplayTask(booster, activator, plugin);
		return booster;
	}
	
	public static BoosterManager activateRandomBooster(Player activator, Plugin plugin) {
		BoosterManager booster = RandomUtils.getRandom(availableBoosters);
		return activateBooster(activator, booster, plugin);
	}
	
	public static BoosterManager activateBooster(Player activator, DyeColor color, Plugin plugin) {
		DisplayColor displayColor = DisplayColor.getFromColor(color);
		if (displayColor == null)
			return activateRandomBooster(activator, plugin);
		List<BoosterManager> boosters = new ArrayList<>();
		for (BoosterManager boost : availableBoosters) 
			if (boost.getDisplayColor() == displayColor)
				boosters.add(boost);
		if (boosters.isEmpty())
			return activateRandomBooster(activator, plugin);
		return activateBooster(activator, RandomUtils.getRandom(boosters), plugin);
	}

	public static boolean registerBooster(BoosterManager booster) throws ConfigFileNotSet, IOException {
		if (!availableBoosters.contains(booster)) {
			if (configFile == null || config == null)
				throw new ConfigFileNotSet("You have to set the config file used to store booster's data before registering a booster.");
			boolean enable = config.getBoolean(booster.getConfigFieldPath("enable"), true);
			if (!enable)
				return false;
			int duration = config.getInt(booster.getConfigFieldPath("life-time"), -1);
			if (duration < 0) {
				config.set(booster.getConfigFieldPath("enable"), true);
				config.set(booster.getConfigFieldPath("life-time"), booster.duration);
				config.save(configFile);
			} else {
				booster.duration = duration;
			}
			availableBoosters.add(booster);
			return true;
		}
		return false;
	}
	
	public static boolean unregisterBooster(BoosterManager booster) {
		if (availableBoosters.contains(booster)) {
			availableBoosters.remove(booster);
			return true;
		}
		return false;
	}
	
	public static void setupConfig(File file)
    {
    	if (!file.exists()) {
    		new FileNotFoundException(file.getName() + " not found. You probably need to create it.").printStackTrace();
    		return;
    	}
    	configFile = file;
    	config = YamlConfiguration.loadConfiguration(file);
    }
	
	public static List<BoosterManager> getAvailableBoosters() {
		return availableBoosters;
	}
}
