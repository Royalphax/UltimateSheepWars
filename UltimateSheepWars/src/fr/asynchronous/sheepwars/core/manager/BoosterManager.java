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
	private DyeColor woolColor;
	private int duration;

	/**
	 * Initialize a new Booster !
	 * 
	 * @param name Display name of this booster.
	 * @param displayColor Display color of this booster.
	 * @param duration Duration of this booster (0 for instant booster such as instant potion effect).
	 */
	public BoosterManager(final String name, final DisplayColor displayColor, final int duration) {
		this(new Message(name), name, displayColor, duration);
	}
	
	/**
	 * Use {@link #BoosterManager(String, DisplayColor, int) this constructor} instead.
	 */
	public BoosterManager(final MsgEnum name, final DisplayColor displayColor, final int duration) {
		this(Message.getMessage(name), name.toString().replaceAll("BOOSTER_", ""), displayColor, duration);
	}
	
	/**
	 * Use {@link #BoosterManager(String, DisplayColor, int) this constructor} instead.
	 */
	public BoosterManager(final Message name, final String configPath, final DisplayColor displayColor, final int duration) {
		this.name = name;
		this.configPath = "booster." + configPath.replaceAll("_", "-").toLowerCase();
		this.displayColor = displayColor;
		this.woolColor = displayColor.getColor();
		this.duration = duration;
	}
	
	/**
	 * No need to use this method.
	 */
	public Message getName() {
		return this.name;
	}
	
	/**
	 * Get booster wool color.
	 */
	public DyeColor getWoolColor() {
		return this.woolColor;
	}

	/**
	 * Get booster display color.
	 */
	public DisplayColor getDisplayColor() {
		return this.displayColor;
	}

	/**
	 * Set booster display color.
	 */
	public void setDisplayColor(DisplayColor displayColor) {
		this.displayColor = displayColor;
	}

	/**
	 * Get booster duration.
	 */
	public int getDuration() {
		return this.duration;
	}

	/**
	 * No need to use this method.
	 */
	private String getConfigFieldPath(String field) {
    	return this.configPath + "." + field;
    }

	/**
	 * Triggered on activation of the booster.
	 * 
	 * @param player Who activate the booster.
	 * @param team Player's team.
	 * @return Boolean value not used yet.
	 */
	public abstract boolean onStart(final Player player, final TeamManager team);

	/**
	 * Triggered on deactivation of the booster.
	 * 
	 * @return Boolean value not used yet.
	 */
	public abstract void onFinish();

	private static BoosterManager activateBooster(Player activator, BoosterManager booster, Plugin plugin) {
		new BoosterDisplayTask(booster, activator, plugin);
		return booster;
	}
	
	/**
	 * Activate a random booster.
	 * 
	 * @param activator Player who activates the booster.
	 * @param plugin Any plugin instance.
	 * @return The booster activated.
	 */
	public static BoosterManager activateRandomBooster(Player activator, Plugin plugin) {
		BoosterManager booster = RandomUtils.getRandom(availableBoosters);
		return activateBooster(activator, booster, plugin);
	}
	
	/**
	 * Activate a booster according to a DyeColor.
	 * 
	 * @param activator Player who activates the booster.
	 * @param plugin Any plugin instance.
	 * @return The booster activated.
	 */
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

	/**
	 * Use {@link fr.asynchronous.sheepwars.core.UltimateSheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
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
	
	/**
	 * Use {@link fr.asynchronous.sheepwars.core.UltimateSheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
	public static boolean unregisterBooster(BoosterManager booster) {
		if (availableBoosters.contains(booster)) {
			availableBoosters.remove(booster);
			return true;
		}
		return false;
	}
	
	/**
	 * No need to use this method.
	 */
	public static void setupConfig(File file)
    {
    	if (!file.exists()) {
    		new FileNotFoundException(file.getName() + " not found. You probably need to create it.").printStackTrace();
    		return;
    	}
    	configFile = file;
    	config = YamlConfiguration.loadConfiguration(file);
    }
	
	/**
	 * Get registered boosters.
	 */
	public static List<BoosterManager> getAvailableBoosters() {
		return availableBoosters;
	}
}
