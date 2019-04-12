package fr.asynchronous.sheepwars.core.sheep;

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

import fr.asynchronous.sheepwars.core.SheepWarsAPI;
import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.usw.SheepGiveEvent;
import fr.asynchronous.sheepwars.core.event.usw.SheepLaunchEvent;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public abstract class SheepWarsSheep {

	private static final double SHEEP_DEFAULT_HEALTH = 8.0;
	private static List<SheepWarsSheep> availableSheeps = new ArrayList<>();
	private static List<SheepWarsSheep> waitingSheeps = new ArrayList<>();
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
		SheepWarsSheep sheep = null;
		while (sheep == null) {
			SheepWarsSheep temp = availableSheeps.get(MathUtils.random.nextInt(availableSheeps.size()));
			if (temp.random >= 1.0f || MathUtils.randomBoolean(temp.random))
				sheep = temp;
		}
		giveSheep(player, sheep);
	}
	
	public static void giveSheep(final Player player, final SheepWarsSheep sheep) {
		giveSheep(player, sheep, 1);
	}

	public static void giveSheep(final Player player, final SheepWarsSheep sheep, final int amount) {
		PlayerData playerData = PlayerData.getPlayerData(player);
		if (playerData.getTeam() != TeamManager.SPEC) {
			if (sheep.onGive(player)) {
				SheepGiveEvent event = new SheepGiveEvent(player, sheep);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					for (int i = 0; i < amount; i++) 
						player.getInventory().addItem(sheep.getIcon(player));
					if (amount > 1) {
						player.sendMessage(Message.getMessage(player, MsgEnum.SEVERAL_SHEEP_RECEIVED).replaceAll("%SHEEP_NAME%", sheep.getName(player)).replaceAll("%AMOUNT%", String.valueOf(amount)));
					} else {
						player.sendMessage(Message.getMessage(player, MsgEnum.SHEEP_RECEIVED).replaceAll("%SHEEP_NAME%", sheep.getName(player)));
					}
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
	public SheepWarsSheep(final String name, final DyeColor color, final int duration, final boolean friendly, final double health, final boolean drop, final float random, final SheepAbility... sheepAbilities) {
		this(new Message(name), name, color, duration, friendly, health, drop, random, sheepAbilities);
	}

	/**
	 * Use {@link #SheepManager(String, DyeColor, int, boolean, double, boolean, float, SheepAbility...) this constructor} instead.
	 */
	public SheepWarsSheep(final MsgEnum name, final DyeColor color, final int duration, final boolean friendly, final boolean drop, final float random, final SheepAbility... sheepAbilities) {
		this(Message.getMessage(name), name.toString().replaceAll("_NAME", ""), color, duration, friendly, SHEEP_DEFAULT_HEALTH, drop, random, sheepAbilities);
	}

	/**
	 * Use {@link #SheepManager(String, DyeColor, int, boolean, double, boolean, float, SheepAbility...) this constructor} instead.
	 */
	public SheepWarsSheep(final MsgEnum name, final DyeColor color, final int duration, final boolean friendly, final boolean drop, final SheepAbility... sheepAbilities) {
		this(Message.getMessage(name), name.toString().replaceAll("_NAME", ""), color, duration, friendly, SHEEP_DEFAULT_HEALTH, drop, 1.0f, sheepAbilities);
	}

	/**
	 * Use {@link #SheepManager(String, DyeColor, int, boolean, double, boolean, float, SheepAbility...) this constructor} instead.
	 */
	public SheepWarsSheep(final Message name, final String configPath, final DyeColor color, final int duration, final boolean friendly, final double health, final boolean drop, final float random, final SheepAbility... sheepAbilities) {
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

	private org.bukkit.entity.Sheep spawnSheep(final Location location, final Player player, Plugin plugin) {
		return SheepWarsPlugin.getVersionManager().getSheepFactory().spawnSheep(location, player, this, plugin);
	}

	/**
	 * Get the sheep name according to the player's language.
	 */
	public String getName(Player player) {
		return this.msgName.getMessage(player);
	}

	/**
	 * Get sheep color.
	 * @return The color of the sheep.
	 */
	public DyeColor getColor() {
		return color;
	}

	/**
	 * Get the sheep icon according to the player's language.
	 */
	public ItemStack getIcon(Player player) {
		return new ItemBuilder(Material.WOOL).setColor(this.color).setName(getName(player)).toItemStack();
	}

	/**
	 * Get the sheep duration.
	 */
	public int getDuration() {
		return this.duration;
	}

	/**
	 * Get if this sheep is friendly or aggressive.
	 */
	public boolean isFriendly() {
		return this.friendly;
	}

	/**
	 * Get the sheep health.
	 */
	public double getHealth() {
		return this.health;
	}

	/**
	 * Get if the sheep is allowed to drop its wool on death.
	 */
	public boolean isDropAllowed() {
		return this.drop;
	}

	/**
	 * Get the luck to get this sheep (between 0.0 and 1.0).
	 */
	public float getRandom() {
		return this.random;
	}

	/**
	 * Get the sheep abilities.
	 */
	public List<SheepAbility> getAbilities() {
		return this.sheepAbilities;
	}
	
	/**
	 * Test if the sheep has the ability.
	 */
	public boolean hasAbility(SheepAbility... abilities) {
		boolean output = true;
		for (SheepAbility ability : abilities)
			if (!this.sheepAbilities.contains(ability))
				output = false;
		return output;
	}

	/**
	 * No need to use this method.
	 */
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
	 * @param ticks Sheep's ticks (number decremented every twentieth of a second starting from (<code>sheep.getDuration*20</code>) at the moment the sheep appeared).
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

	/**
	 * Throw this sheep from a player.
	 */
	public boolean throwSheep(Player launcher, Plugin plugin) {
		Location playerLocation = launcher.getEyeLocation().clone();
		Location location = playerLocation.toVector().add(playerLocation.getDirection().multiply(0.5)).toLocation(launcher.getWorld());

		Sheep entity = spawnSheep(location, launcher, plugin);
		entity.setAdult();

		SheepWarsPlugin.getVersionManager().getNMSUtils().setHealth(entity, this.health);
		entity.setMetadata(SheepWarsAPI.SHEEPWARS_SHEEP_METADATA, new FixedMetadataValue(plugin, true));

		SheepLaunchEvent event = new SheepLaunchEvent(launcher, entity, this);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			entity.remove();
			return false;
		} else {
			if (!this.friendly)
				//entity.setVelocity(launcher.getEyeLocation().getDirection().clone().multiply(3.5));
				entity.setVelocity(playerLocation.getDirection().multiply(ConfigManager.getDouble(Field.SHEEP_VELOCITY)));
			Sounds.playSoundAll(location, Sounds.HORSE_SADDLE, 1f, 1f);
			return true;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SheepWarsSheep)) {
			return false;
		}
		SheepWarsSheep other = (SheepWarsSheep) obj;
		return this.msgName == other.msgName && this.color == other.color && this.duration == other.duration && this.friendly == other.friendly && this.health == other.health && this.drop == other.drop && this.random == other.random && this.sheepAbilities == other.sheepAbilities;
	}

	public static SheepWarsSheep getCorrespondingSheep(ItemStack item, Player player) {
		for (SheepWarsSheep sheep : availableSheeps) {
			if (item.isSimilar(sheep.getIcon(player)))
				return sheep;
		}
		return null;
	}

	/**
	 * Get registered sheeps.
	 */
	public static List<SheepWarsSheep> getAvailableSheeps() {
		return new ArrayList<>(availableSheeps);
	}

	/**
	 * Use {@link fr.asynchronous.sheepwars.core.SheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
	public static boolean registerSheep(SheepWarsSheep sheep) throws IOException {
		if (!availableSheeps.contains(sheep)) {
			if (configFile == null || config == null) {
				waitingSheeps.add(sheep);
				return false;
			}
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

	/**
	 * Use {@link fr.asynchronous.sheepwars.core.SheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
	public static boolean unregisterSheep(SheepWarsSheep sheep) {
		if (availableSheeps.contains(sheep)) {
			availableSheeps.remove(sheep);
			return true;
		}
		return false;
	}

	/**
	 * No need to use this method.
	 */
	public static void setupConfig(File file, SheepWarsPlugin plugin) {
		if (!file.exists()) {
			new FileNotFoundException(file.getName() + " not found. You probably need to create it.").printStackTrace();
			return;
		}
		configFile = file;
		config = YamlConfiguration.loadConfiguration(file);
		for (SheepWarsSheep sheep : waitingSheeps)
			try {
				registerSheep(sheep);
				plugin.getLogger().info("Custom Sheep : " + sheep.getClass().getName() + " fully registred!");
			} catch (IOException e) {
				plugin.getLogger().info("Can't register custom sheep " + sheep.getClass().getName() + ", an error occured!");
				new ExceptionManager(e).register(true);
			}
		waitingSheeps.clear();
	}
}
