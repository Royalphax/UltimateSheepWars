package fr.asynchronous.sheepwars.core.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.exception.ConfigFileNotSet;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.task.BoosterTask;

public abstract class BoosterManager {
	private static List<BoosterManager> availableBoosters = new ArrayList<>();
	private static BoosterManager activatedBooster;
	private static File configFile;
    private static FileConfiguration config;

	private final Message name;
	private final String configPath;
	private DisplayColor displayColor;
	private int duration;
	private final List<TriggerBoosterAction> triggers;

	public BoosterManager(final String name, final DisplayColor displayColor, final int duration, final TriggerBoosterAction... triggers) {
		this(new Message(name), name, displayColor, duration, triggers);
	}
	
	public BoosterManager(final MsgEnum name, final DisplayColor displayColor, final int duration, final TriggerBoosterAction... triggers) {
		this(Message.getMessage(name), name.toString().replaceAll("BOOSTER_", ""), displayColor, duration, triggers);
	}
	
	public BoosterManager(final Message name, final String configPath, final DisplayColor displayColor, final int duration, final TriggerBoosterAction... triggers) {
		this.name = name;
		this.configPath = "booster." + configPath.replaceAll("_", "-").toLowerCase();
		this.displayColor = displayColor;
		this.duration = duration;
		this.triggers = Arrays.<TriggerBoosterAction>asList(triggers);
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

	public List<TriggerBoosterAction> getTriggers() {
		return this.triggers;
	}
	
	private String getConfigFieldPath(String field) {
    	return this.configPath + "." + field;
    }

	public static enum TriggerBoosterAction {
		OTHER,
		ARROW_LAUNCH;
	}

	public abstract boolean onStart(final Player player, final TeamManager team);

	public abstract void onEvent(final Player player, final Event event, final TriggerBoosterAction triggerAction);

	public abstract void onFinish();

	public static BoosterManager activateRandomBooster(Player activator, Plugin plugin) {
		Random rdm = new Random();
		activatedBooster = availableBoosters.get(rdm.nextInt(availableBoosters.size()));
		new BoosterTask(activatedBooster, activator, plugin);
		return activatedBooster;
	}

	public static BoosterManager getActivatedBooster() {
		return activatedBooster;
	}

	public static boolean isBoosterActivated() {
		return (activatedBooster != null);
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
}
