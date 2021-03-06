package fr.royalpha.sheepwars.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.royalpha.sheepwars.core.handler.DisplayColor;
import fr.royalpha.sheepwars.core.manager.ExceptionManager;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.DyeColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.task.BoosterDisplayTask;
import fr.royalpha.sheepwars.core.util.RandomUtils;

public abstract class SheepWarsBooster implements Listener {
	
	private static List<SheepWarsBooster> availableBoosters = new ArrayList<>();
	private static List<SheepWarsBooster> waitingBoosters = new ArrayList<>();
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
	 * @param displayColor Display color of this booster (wool color will be the same).
	 * @param duration Duration of this booster (0 for instant booster such as instant potion effect).
	 */
	public SheepWarsBooster(final String name, final DisplayColor displayColor, final int duration) {
		this(name, displayColor, displayColor.getColor(), duration);
	}
	
	/**
	 * Initialize a new Booster !
	 * 
	 * @param name Display name of this booster.
	 * @param displayColor Display color of this booster (boss bar and chat color).
	 * @param woolColor Wool color of this booster.
	 * @param duration Duration of this booster (0 for instant booster such as instant potion effect).
	 */
	public SheepWarsBooster(final String name, final DisplayColor displayColor, DyeColor woolColor, final int duration) {
		this(new Message(name), name, displayColor, woolColor, duration);
	}
	
	/**
	 * Use {@link #SheepWarsBooster(String, DisplayColor, int) this constructor} instead.
	 */
	public SheepWarsBooster(final Message.Messages name, final DisplayColor displayColor, final int duration) {
		this(name, displayColor, displayColor.getColor(), duration);
	}
	
	/**
	 * Use {@link #SheepWarsBooster(String, DisplayColor, int) this constructor} instead.
	 */
	public SheepWarsBooster(final Message.Messages name, final DisplayColor displayColor, DyeColor woolColor, final int duration) {
		this(Message.getMessage(name), name.toString().replaceAll("BOOSTER_", ""), displayColor, woolColor, duration);
	}
	
	/**
	 * Use {@link #SheepWarsBooster(String, DisplayColor, int) this constructor} instead.
	 */
	public SheepWarsBooster(final Message name, final String configPath, final DisplayColor displayColor, DyeColor woolColor, final int duration) {
		this.name = name;
		this.configPath = "booster." + configPath.replaceAll("_", "-").toLowerCase();
		this.displayColor = displayColor;
		this.woolColor = woolColor;
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
	public abstract boolean onStart(final Player player, final SheepWarsTeam team);

	/**
	 * Triggered on deactivation of the booster.
	 * 
	 * @return Boolean value not used yet.
	 */
	public abstract void onFinish();

	private static SheepWarsBooster activateBooster(Player activator, SheepWarsBooster booster, Plugin plugin) {
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
	public static SheepWarsBooster activateRandomBooster(Player activator, Plugin plugin) {
		SheepWarsBooster booster = RandomUtils.getRandom(availableBoosters);
		return activateBooster(activator, booster, plugin);
	}
	
	/**
	 * Activate a booster according to a DyeColor.
	 * 
	 * @param activator Player who activates the booster.
	 * @param plugin Any plugin instance.
	 * @return The booster activated.
	 */
	public static SheepWarsBooster activateBooster(Player activator, DyeColor color, Plugin plugin) {
		DisplayColor displayColor = DisplayColor.getFromColor(color);
		if (displayColor == null)
			return activateRandomBooster(activator, plugin);
		List<SheepWarsBooster> boosters = new ArrayList<>();
		for (SheepWarsBooster boost : availableBoosters) 
			if (boost.getDisplayColor() == displayColor)
				boosters.add(boost);
		if (boosters.isEmpty())
			return activateRandomBooster(activator, plugin);
		return activateBooster(activator, RandomUtils.getRandom(boosters), plugin);
	}

	/**
	 * Use {@link SheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
	public static boolean registerBooster(SheepWarsBooster booster) throws IOException {
		if (!availableBoosters.contains(booster)) {
			if (configFile == null || config == null) {
				waitingBoosters.add(booster);
				return false;
			}
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
	 * Use {@link SheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
	public static boolean unregisterBooster(SheepWarsBooster booster) {
		if (availableBoosters.contains(booster)) {
			availableBoosters.remove(booster);
			return true;
		}
		return false;
	}
	
	/**
	 * No need to use this method.
	 */
	public static void setupConfig(File file, SheepWarsPlugin plugin)
    {
    	if (!file.exists()) {
    		new FileNotFoundException(file.getName() + " not found. You probably need to create it.").printStackTrace();
    		return;
    	}
    	configFile = file;
    	config = YamlConfiguration.loadConfiguration(file);
    	for (SheepWarsBooster booster : waitingBoosters)
			try {
				registerBooster(booster);
				plugin.getLogger().info("Custom Booster : " + booster.getClass().getName() + " fully registred!");
			} catch (IOException e) {
				plugin.getLogger().info("Can't register custom booster " + booster.getClass().getName() + ", an error occured!");
				ExceptionManager.register(e, true);
			}
    	waitingBoosters.clear();
    }
	
	/**
	 * Get registered boosters.
	 */
	public static List<SheepWarsBooster> getAvailableBoosters() {
		return new ArrayList<>(availableBoosters);
	}
}
