package fr.asynchronous.sheepwars.core.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.exception.ConfigFileNotSet;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.kit.MoreSheepKit;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public abstract class SheepManager {

	private static List<SheepManager> availableSheeps = new ArrayList<>();
	private static File configFile;
    private static FileConfiguration config;

	private Message msgName;
	private String configPath;
	private DyeColor color;
	private int duration;
	private boolean friendly;
	private double health;
	private boolean drop;
	private float random;
	private List<SheepAbility> sheepAbilities;

	public static void giveRandomSheep(final Player player) {
		PlayerData playerData = PlayerData.getPlayerData(player);
		for (int i = 0; i < ((playerData.getKit() == new MoreSheepKit() && RandomUtils.getRandomByPercent(MoreSheepKit.CHANCE_TO_GET_ONE_MORE_SHEEP)) ? 2 : 1); i++) {
			SheepManager sheep = null;
			while (sheep == null) {
				SheepManager temp = availableSheeps.get(MathUtils.random.nextInt(availableSheeps.size()));
				if (temp.random >= 1.0f || MathUtils.randomBoolean(temp.random))
					sheep = temp;
			}
			giveSheep(player, sheep);
		}
	}

	public static void giveSheep(final Player player, final SheepManager sheep) {
		PlayerData playerData = PlayerData.getPlayerData(player);
		if (playerData.getTeam() != TeamManager.SPEC) {
			if (sheep.onGive(player)) {
				player.getInventory().addItem(sheep.getIcon(player));
				player.sendMessage(Message.getMessage(player, MsgEnum.SHEEP_RECEIVED).replaceAll("%SHEEP_NAME%", sheep.getName(player)));
			} else {
				giveRandomSheep(player);
			}
		}
	}
	
	public SheepManager(final String name, final DyeColor color, final int duration, final boolean friendly, final double health, final boolean drop, final float random, final SheepAbility... sheepAbilities) {
		this(new Message(name), name, color, duration, friendly, health, drop, random, sheepAbilities);
	}
	
	public SheepManager(final MsgEnum name, final DyeColor color, final int duration, final boolean friendly, final double health, final boolean drop, final float random, final SheepAbility... sheepAbilities) {
		this(Message.getMessageByEnum(name), name.toString().replaceAll("_NAME", ""), color, duration, friendly, health, drop, random, sheepAbilities);
	}

	public SheepManager(final Message name, final String configPath, final DyeColor color, final int duration, final boolean friendly, final double health, final boolean drop, final float random, final SheepAbility... sheepAbilities) {
		this.msgName = name;
		this.configPath = "sheep." + configPath.replaceAll("_", "-").toLowerCase();
		this.color = color;
		this.duration = duration;
		this.friendly = friendly;
		this.health = health;
		this.drop = drop;
		this.random = random;
		this.sheepAbilities = Arrays.<SheepAbility>asList(sheepAbilities);
	}

	public org.bukkit.entity.Sheep spawnSheep(final Location location, final Player player, Plugin plugin) {
		return UltimateSheepWarsPlugin.getVersionManager().getSheepFactory().spawnSheep(location, player, this, plugin);
	}

	public String getName(Player player) {
		return this.msgName.getMessage(player);
	}

	public DyeColor getColor() {
		return color;
	}

	public ItemStack getIcon(Player player) {
		return new ItemBuilder(Material.WOOL).setWoolColor(this.color).setName(getName(player)).toItemStack();
	}

	public int getDuration() {
		return this.duration;
	}

	public boolean isFriendly() {
		return this.friendly;
	}
	
	public double getHealth() {
		return this.health;
	}

	public boolean isDrop() {
		return this.drop;
	}

	public float getRandom() {
		return this.random;
	}
	
	public List<SheepAbility> getAbilities() {
		return this.sheepAbilities;
	}
	
	private String getConfigFieldPath(String field) {
    	return this.configPath + "." + field;
    }

	public abstract boolean onGive(final Player player);

	public abstract void onSpawn(final Player player, final org.bukkit.entity.Sheep bukkitSheep, final Plugin plugin);

	public abstract boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep bukkitSheep, final Plugin plugin);

	public abstract void onFinish(final Player player, final org.bukkit.entity.Sheep bukkitSheep, final boolean death, final Plugin plugin);

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SheepManager)) {
			return false;
		}
		SheepManager other = (SheepManager) obj;
		return this.msgName == other.msgName 
				&& this.color == other.color 
				&& this.duration == other.duration 
				&& this.friendly == other.friendly 
				&& this.health == other.health
				&& this.drop == other.drop 
				&& this.random == other.random
				&& this.sheepAbilities == other.sheepAbilities;
	}
	
	public static List<SheepManager> getAvailableSheeps() {
		return availableSheeps;
	}

	public static boolean registerSheep(SheepManager sheep) throws ConfigFileNotSet, IOException {
		if (!availableSheeps.contains(sheep)) {
			if (configFile == null || config == null)
				throw new ConfigFileNotSet("You have to set the config file used to store booster's data before registering a booster.");
			boolean enable = config.getBoolean(sheep.getConfigFieldPath("enable"), true);
			if (!enable)
				return false;
			DyeColor color = DyeColor.valueOf(config.getString(sheep.getConfigFieldPath("color"), "WHITE"));
			int duration = config.getInt(sheep.getConfigFieldPath("life-time"), -1);
			double health = config.getDouble(sheep.getConfigFieldPath("health"), -1.0);
			double random = config.getDouble(sheep.getConfigFieldPath("random"), -1.0);
			if (duration < 0 || health < 0 || random < 0) {
				config.set(sheep.getConfigFieldPath("enable"), true);
				config.set(sheep.getConfigFieldPath("color"), sheep.color.toString());
				config.set(sheep.getConfigFieldPath("life-time"), sheep.duration);
				config.set(sheep.getConfigFieldPath("health"), sheep.health);
				config.set(sheep.getConfigFieldPath("random"), sheep.random);
				config.save(configFile);
			} else {
				sheep.color = color;
				sheep.duration = duration;
				sheep.health = health;
				sheep.random = (float) random;
			}
			availableSheeps.add(sheep);
			return true;
		}
		return false;
	}
	
	public static boolean unregisterSheep(SheepManager sheep) {
		if (availableSheeps.contains(sheep)) {
			availableSheeps.remove(sheep);
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
