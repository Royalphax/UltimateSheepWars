package fr.asynchronous.sheepwars.core.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsAPI;
import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.usw.SheepGiveEvent;
import fr.asynchronous.sheepwars.core.event.usw.SheepLaunchEvent;
import fr.asynchronous.sheepwars.core.exception.ConfigFileNotSet;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public abstract class SheepManager {

	private static final double SHEEP_DEFAULT_HEALTH = 8.0;
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
		SheepManager sheep = null;
		while (sheep == null) {
			SheepManager temp = availableSheeps.get(MathUtils.random.nextInt(availableSheeps.size()));
			if (temp.random >= 1.0f || MathUtils.randomBoolean(temp.random))
				sheep = temp;
		}
		giveSheep(player, sheep);
	}

	public static void giveSheep(final Player player, final SheepManager sheep) {
		PlayerData playerData = PlayerData.getPlayerData(player);
		if (playerData.getTeam() != TeamManager.SPEC) {
			if (sheep.onGive(player)) {
				SheepGiveEvent event = new SheepGiveEvent(player, sheep);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					player.getInventory().addItem(sheep.getIcon(player));
					player.sendMessage(Message.getMessage(player, MsgEnum.SHEEP_RECEIVED).replaceAll("%SHEEP_NAME%", sheep.getName(player)));
				}
			} else {
				giveRandomSheep(player);
			}
		}
	}

	/**
	 * Initialize a new sheep !
	 * 
	 * @param name Define the sheep's name. It will be automatically add to the language files.
	 * @param color Color of the sheep.
	 * @param duration Life time (in seconds) of the sheep, 0 will cause the sheep not to be automatically killed.
	 * @param friendly Is this sheep friendly ? If yes, it will not be launched but just appear at the player position. 
	 * @param health Sheep's health.
	 * @param drop Will the sheep drop its wool if killed ?
	 * @param random Specify the luck to get this sheep (1 = maximum luck, 0 = you will never get this sheep).
	 * @param sheepAbilities (Optional) Sheep ability such as "FIRE_PROOF" or "SEEK_PLAYERS".
	 */
	public SheepManager(final String name, final DyeColor color, final int duration, final boolean friendly, final double health, final boolean drop, final float random, final SheepAbility... sheepAbilities) {
		this(new Message(name), name, color, duration, friendly, health, drop, random, sheepAbilities);
	}

	public SheepManager(final MsgEnum name, final DyeColor color, final int duration, final boolean friendly, final boolean drop, final float random, final SheepAbility... sheepAbilities) {
		this(Message.getMessage(name), name.toString().replaceAll("_NAME", ""), color, duration, friendly, SHEEP_DEFAULT_HEALTH, drop, random, sheepAbilities);
	}

	public SheepManager(final MsgEnum name, final DyeColor color, final int duration, final boolean friendly, final boolean drop, final SheepAbility... sheepAbilities) {
		this(Message.getMessage(name), name.toString().replaceAll("_NAME", ""), color, duration, friendly, SHEEP_DEFAULT_HEALTH, drop, 1.0f, sheepAbilities);
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
		return new ItemBuilder(Material.WOOL).setColor(this.color).setName(getName(player)).toItemStack();
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

	public boolean isDropAllowed() {
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

	/**
	 * Useful to cancel the give of a sheep under certain conditions.
	 * 
	 * @param player Player who will receive the sheep in his inventory
	 * @return <b>true</b> if there's no problem giving that sheep to that player. <b>false</b> otherwise and the sheep won't be given.
	 */
	public abstract boolean onGive(final Player player);

	/**
	 * Useful to do something when the sheep gets launched.
	 * <br/><u>For example :</u> the Intergalactic Sheep broadcast a message.
	 * 
	 * @param player 
	 * @param bukkitSheep The sheep's bukkit entity.
	 * @param plugin UltimateSheepWars plugin instance (useful to launch runnables).
	 */
	public abstract void onSpawn(final Player player, final org.bukkit.entity.Sheep bukkitSheep, final Plugin plugin);
	
	/**
	 * Most important method called every tick.
	 * 
	 * @param player Player who has launched the sheep.
	 * @param ticks Sheep's ticks (number incremented every twentieth of a second starting from 0 at the moment the sheep appeared).
	 * @param bukkitSheep The sheep's bukkit entity.
	 * @param plugin UltimateSheepWars plugin instance (useful to launch runnables).
	 * @return <b>true</b> if you want to stop the sheep and directly go to the <i>onFinish([...]);</i> method, <b>false</b> if you want to continue ticking.
	 */
	public abstract boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep bukkitSheep, final Plugin plugin);

	/**
	 * Called before the sheep disappear.
	 * 
	 * @param player Player who has launched the sheep.
	 * @param bukkitSheep The sheep's bukkit entity.
	 * @param death Was the sheep killed by a player and is already dead ?
	 * @param plugin UltimateSheepWars plugin instance (useful to launch runnables).
	 */
	public abstract void onFinish(final Player player, final org.bukkit.entity.Sheep bukkitSheep, final boolean death, final Plugin plugin);

	public boolean throwSheep(Player launcher, Plugin plugin) {
		Location playerLocation = launcher.getLocation().add(0, 2, 0);
		Location location = playerLocation.toVector().add(playerLocation.getDirection().multiply(0.5)).toLocation(launcher.getWorld());

		Sheep entity = spawnSheep(location, launcher, plugin);
		entity.setAdult();

		UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setHealth(entity, this.health);
		entity.setMetadata(UltimateSheepWarsAPI.SHEEPWARS_SHEEP_METADATA, new FixedMetadataValue(plugin, true));

		SheepLaunchEvent event = new SheepLaunchEvent(launcher, entity, this);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			entity.remove();
			return false;
		} else {
			if (!this.friendly)
				entity.setVelocity(playerLocation.getDirection().add(new Vector(0, 0.1, 0)).multiply(ConfigManager.getDouble(Field.SHEEP_VELOCITY)));
			Sounds.playSoundAll(location, Sounds.HORSE_SADDLE, 1f, 1f);
			return true;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SheepManager)) {
			return false;
		}
		SheepManager other = (SheepManager) obj;
		return this.msgName == other.msgName && this.color == other.color && this.duration == other.duration && this.friendly == other.friendly && this.health == other.health && this.drop == other.drop && this.random == other.random && this.sheepAbilities == other.sheepAbilities;
	}

	public static SheepManager getCorrespondingSheep(ItemStack item, Player player) {
		for (SheepManager sheep : availableSheeps) {
			if (item.isSimilar(sheep.getIcon(player)))
				return sheep;
		}
		return null;
	}

	public static List<SheepManager> getAvailableSheeps() {
		return availableSheeps;
	}

	public static boolean registerSheep(SheepManager sheep) throws ConfigFileNotSet, IOException {
		if (!availableSheeps.contains(sheep)) {
			if (configFile == null || config == null)
				throw new ConfigFileNotSet("You have to set the config file used to store sheep's data before registering a custom sheep.");
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

	public static void setupConfig(File file) {
		if (!file.exists()) {
			new FileNotFoundException(file.getName() + " not found. You probably need to create it.").printStackTrace();
			return;
		}
		configFile = file;
		config = YamlConfiguration.loadConfiguration(file);
	}
}
