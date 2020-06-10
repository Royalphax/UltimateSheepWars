package fr.asynchronous.sheepwars.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.exception.UnknownKitException;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.api.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.kit.NoneKit;
import fr.asynchronous.sheepwars.core.kit.NoneKit.NullKitLevel;
import fr.asynchronous.sheepwars.core.kit.RandomKit;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public abstract class SheepWarsKit {
	private static ArrayList<SheepWarsKit> availableKits = new ArrayList<>();
	private static ArrayList<SheepWarsKit> waitingKits = new ArrayList<>();
	private static File configFile;
	private static FileConfiguration config;

	private int id;
	private String configPath;
	private Message name;
	private ItemBuilder icon;
	private boolean freeKit;
	private Plugin plugin;

	private LinkedList<SheepWarsKitLevel> levels = new LinkedList<>();

	/**
	 * Initialize a new Kit !
	 * 
	 * @param id
	 *            Unique ID of this Kit. Default kits : 0,1,2,->9.
	 * @param name
	 *            Name of this Kit.
	 * @param icon
	 *            Icon of this Kit.
	 * @param levels
	 *            Levels of the kit.
	 */
	public SheepWarsKit(final int id, final String name, final ItemBuilder icon, final SheepWarsKitLevel... levels) {
		this(id, name, new Message(name), false, icon, levels);
	}

	/**
	 * Use {@link #SheepWarsKit(int, String, ItemBuilder, SheepWarsKitLevel...) this constructor} instead.
	 */
	public SheepWarsKit(final int id, final Messages name, final ItemBuilder icon, final SheepWarsKitLevel... levels) {
		this(id, name.toString().replaceAll("KIT_", "").replaceAll("_NAME", ""), Message.getMessage(name), false, icon, levels);
	}

	/**
	 * Initialize a new Kit !
	 * 
	 * @param id
	 *            Unique ID of this Kit. Default kits : 0,1,2,->9.
	 * @param name
	 *            Name of this Kit.
	 * @param freeKit
	 *            Is this kit free or not a kit ?
	 * @param icon
	 *            Icon of this Kit.
	 * @param levels
	 *            Levels of the kit.
	 */
	public SheepWarsKit(final int id, final String name, final boolean freeKit, final ItemBuilder icon, final SheepWarsKitLevel... levels) {
		this(id, name, new Message(name), freeKit, icon, levels);
	}

	/**
	 * Use {@link #SheepWarsKit(int, String, boolean, ItemBuilder, SheepWarsKitLevel...) this constructor} instead.
	 */
	public SheepWarsKit(final int id, final Messages name, final boolean freeKit, final ItemBuilder icon, final SheepWarsKitLevel... levels) {
		this(id, name.toString().replaceAll("KIT_", "").replaceAll("_NAME", ""), Message.getMessage(name), freeKit, icon, levels);
	}

	/**
	 * Use {@link #SheepWarsKit(int, String, boolean, ItemBuilder, SheepWarsKitLevel...) this constructor} instead.
	 */
	public SheepWarsKit(final int id, final String configPath, final Message name, final boolean freeKit, final ItemBuilder icon, final SheepWarsKitLevel... levels) {
		this.id = id;
		this.configPath = "kit." + Message.getRaw(configPath);
		this.name = name;
		this.freeKit = freeKit;
		this.icon = icon;
		for (int i = 0; i < levels.length; i++)
			this.levels.add(levels[i]);
	}

	/**
	 * Get the kit name according to the player's language.
	 */
	public String getName(Player player) {
		return this.name.getMessage(player);
	}

	/**
	 * Get the kit name according to the (player's) language.
	 */
	public String getName(Language lang) {
		return this.name.getMessage(lang);
	}

	/**
	 * Get the kit icon.
	 */
	public ItemBuilder getIcon() {
		final ItemBuilder iconBis = new ItemBuilder(this.icon);
		return iconBis;
	}

	public SheepWarsKitLevel getLevel(final Player player) {
		SheepWarsKitLevel current = new NullKitLevel();
		for (int i = 0; i < this.levels.size(); i++) {
			final SheepWarsKitLevel level = this.levels.get(i);
			if (level.canUseLevel(player).contains(KitResult.ALREADY_OWNED))
				current = level;
		}
		return current;
	}

	/**
	 * Get the level classes of this kit.
	 */
	public LinkedList<SheepWarsKitLevel> getLevels() {
		return new LinkedList<>(this.levels);
	}

	/**
	 * Plugin who register this kit.
	 */
	public Plugin getPlugin() {
		return this.plugin;
	}

	/**
	 * Get if this kit id is equal to another kit id.
	 */
	public boolean isKit(SheepWarsKit kit) {
		return (this.id == kit.getId());
	}

	public boolean hasLevel(int level) {
		if (level >= this.levels.size() || level < 0)
			return false;
		return true;
	}

	public SheepWarsKitLevel getLevel(int level) {
		if (level >= this.levels.size())
			throw new IndexOutOfBoundsException("The input level is too high for this kit (" + level + " > " + (this.levels.size() - 1) + ").");
		return this.levels.get(level);
	}

	/**
	 * Get kit id.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Is this kit free or not a kit ?
	 */
	public boolean isFreeKit() {
		return this.freeKit;
	}

	/**
	 * No need to use this method.
	 */
	private String getConfigFieldPath(String field) {
		return this.configPath + "." + field;
	}

	/**
	 * Get if this ID is linked to a kit.
	 */
	public static boolean existKit(int id) {
		return !(getFromId(id) == null);
	}

	/**
	 * Get a Kit from an ID.
	 */
	public static SheepWarsKit getFromId(int id) {
		for (SheepWarsKit kit : availableKits)
			if (kit.getId() == id)
				return kit;
		return null;
	}

	/**
	 * Get the same instance of a Kit.
	 * @throws UnknownKitException 
	 */
	public static SheepWarsKit getInstanceKit(SheepWarsKit kit) throws UnknownKitException {
		for (SheepWarsKit k : availableKits) {
			if (k.getId() == kit.getId()) {
				return k;
			}
		}
		throw new UnknownKitException("No instance was declared for the input kit before.");
	}

	/**
	 * Get registered kits.
	 */
	public static List<SheepWarsKit> getAvailableKits() {
		return new ArrayList<>(availableKits);
	}

	/**
	 * Use {@link SheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
	public static boolean registerKit(SheepWarsKit kit, Plugin plugin) throws IOException {
		if (!availableKits.contains(kit)) {
			if (kit.levels.isEmpty()) {
				plugin.getLogger().warning("You can't register a kit which has no level. You must specify at least one level class in the constructor (" + kit.getClass().getCanonicalName() + ").");
				return false;
			}
			if (kit.levels.size() > 1 && kit.freeKit) {
				plugin.getLogger().warning("You can't register a free kit wich has more than only one level (" + kit.getClass().getCanonicalName() + ").");
				return false;
			}
			if (configFile == null || config == null) {
				waitingKits.add(kit);
				return false;
			}
			boolean enable = config.getBoolean(kit.getConfigFieldPath("enable"), true);
			boolean hasntToBeRegistred = (kit.isKit(new RandomKit()) || kit.isKit(new NoneKit()));
			boolean edited = false;
			for (int id = 0; id < kit.levels.size(); id++) {
				SheepWarsKitLevel level = kit.levels.get(id);
				level.parentKit = kit;
				level.id = id;
				if (!hasntToBeRegistred) {
					String subpath = "level-" + id + ".";
					double price = config.getDouble(kit.getConfigFieldPath(subpath + "price"), -1.0);
					int requiredWins = config.getInt(kit.getConfigFieldPath(subpath + "required-wins"), -1);
					if (price < 0.0 || requiredWins < 0.0) {
						config.set(kit.getConfigFieldPath(subpath + "price"), level.getPrice());
						config.set(kit.getConfigFieldPath(subpath + "required-wins"), level.getRequiredWins());
						if (!edited)
							edited = true;
					} else {
						level.price = price;
						level.wins = requiredWins;
					}
				}
			}
			if (edited) {
				config.set(kit.getConfigFieldPath("enable"), true);
				config.save(configFile);
			}
			if (!enable)
				return false;
			kit.plugin = plugin;
			/*
			 * for (KitLevel lvl : kit.getLevels()) { Bukkit.getPluginManager().registerEvents(lvl, plugin); // Déjà fait au lancement du jeu dans la PreGameTask }
			 */
			availableKits.add(kit);
			return true;
		}
		return false;
	}

	/**
	 * Use {@link SheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
	public static boolean unregisterKit(SheepWarsKit kit) {
		if (availableKits.contains(kit)) {
			availableKits.remove(kit);
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
		for (SheepWarsKit kit : waitingKits)
			try {
				registerKit(kit, plugin);
				plugin.getLogger().info("Custom Kit : " + kit.getClass().getName() + " fully registred!");
			} catch (IOException e) {
				plugin.getLogger().info("Can't register custom kit " + kit.getClass().getName() + ", an error occured!");
				ExceptionManager.register(e, true);
			}
		waitingKits.clear();
	}

	/**
	 * Used to create different levels for a kit. <br/>
	 * Example :
	 * <ul>
	 * <li>MoreSheep kit</li>
	 * <ul>
	 * <li>Level 1 - gives you one more sheep</li>
	 * <li>Level 2 - gives you two more sheep</li>
	 * <li>Level 3 - gives you three more sheep</li>
	 * </ul>
	 * </ul>
	 * Each class representing one level of a kit have to extend KitLevel.
	 * 
	 * @author Roytreo28
	 */
	public static abstract class SheepWarsKitLevel implements Listener {

		private int id;
		private SheepWarsKit parentKit;

		final private Message name;
		final private Message description;
		final private String permission;
		private Double price;
		private int wins;

		// LE CAS OU NAME ET DESC SON STRING

		public SheepWarsKitLevel(final String description, final String permission, final double price, final int requiredWins) {
			this(Message.getMessage(Messages.KIT_LEVEL_DEFAULT_NAME), new Message(description), permission, price, requiredWins);
		}

		public SheepWarsKitLevel(final String levelName, final String description, final String permission, final double price, final int requiredWins) {
			this(new Message(levelName), new Message(description), permission, price, requiredWins);
		}

		// LE CAS OU NAME EST STRING ET DESC EST MSGENUM

		public SheepWarsKitLevel(final String levelName, final Messages description, final String permission, final double price, final int requiredWins) {
			this(new Message(levelName), Message.getMessage(description), permission, price, requiredWins);
		}

		// LE CAS OU NAME EST MSGENUM ET DESC EST STRING

		public SheepWarsKitLevel(final Messages levelName, final String description, final String permission, final double price, final int requiredWins) {
			this(Message.getMessage(levelName), new Message(description), permission, price, requiredWins);
		}

		// LE CAS OU NAME ET DESC SON MSGENUM

		public SheepWarsKitLevel(final Messages description, final String permission, final double price, final int requiredWins) {
			this(Messages.KIT_LEVEL_DEFAULT_NAME, description, permission, price, requiredWins);
		}

		public SheepWarsKitLevel(final Messages levelName, final Messages description, final String permission, final double price, final int requiredWins) {
			this(Message.getMessage(levelName), Message.getMessage(description), permission, price, requiredWins);
		}

		public SheepWarsKitLevel(final Message levelName, final Message description, final String permission, final double price, final int requiredWins) {
			this.name = levelName;
			this.description = description;
			this.permission = permission;
			this.price = price;
			this.wins = requiredWins;
		}

		/**
		 * @return the level description according to the input (player's) language.
		 */
		public String getDescription(Language lang) {
			return replaceAllPlaceholders(this.description.getMessage(lang), lang);
		}

		/**
		 * @return the level name according to the input (player's) language.
		 */
		public String getName(Language lang) {
			return replaceAllPlaceholders(this.name.getMessage(lang), lang);
		}

		/**
		 * Used to replace %LEVEL_NAME% and %LEVEL_ID% by their respective values.
		 */
		private String replaceAllPlaceholders(final String input, final Language lang) {
			String output = input.replaceAll("%LEVEL_ID%", String.valueOf(this.id));
			output = output.replaceAll("%LEVEL_NAME%", this.name.getMessage(lang));
			return output;
		}

		/**
		 * @return the parent kit
		 */
		public SheepWarsKit getParentKit() {
			return this.parentKit;
		}

		/**
		 * Get the permission needed to use this level if permissions are enabled in UltimateSheepWars's config file.
		 */
		public String getPermission() {
			return this.permission;
		}

		/**
		 * Get the amount of required wins to use this level if required wins is enabled in the UltimateSheepWars's config file.
		 */
		public int getRequiredWins() {
			return this.wins;
		}

		/**
		 * @return parent kit's id
		 */
		public int getKitId() {
			return this.parentKit.getId();
		}

		/**
		 * @return parent kit's owning plugin
		 */
		public Plugin getPlugin() {
			return this.parentKit.getPlugin();
		}

		/**
		 * Get the price of this level if ingame-shop is enabled in the UltimateSheepWars's config file.
		 */
		public Double getPrice() {
			return this.price;
		}

		/**
		 * Get kit level.
		 */
		public int getLevelId() {
			return this.id;
		}

		/**
		 * Triggered when the game begins.
		 * 
		 * @param player
		 *            Player who chose this kit.
		 * @return <b>true</b> if you want to let the plugin equiping all the default things to the player (leather armor, sword, bow, etc.). <b>false</b> if you want to set a special inventory content to the player.
		 */
		public abstract boolean onEquip(final Player player);

		/**
		 * @return if a player can use this level of the kit.
		 */
		public List<KitResult> canUseLevel(Player player) {
			List<KitResult> output = new ArrayList<>();
			PlayerData data = PlayerData.getPlayerData(player);
			if (ConfigManager.getBoolean(Field.ENABLE_ALL_KITS)) {
				output.add(KitResult.ALREADY_OWNED);
				return output;
			}
			if (this.parentKit.isFreeKit() || data.getKitLevel(this.parentKit) >= this.id || (ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS) && data.getWins() >= this.wins) || (ConfigManager.getBoolean(Field.ENABLE_KIT_PERMISSIONS) && player.hasPermission(this.permission)) || (Contributor.isDeveloper(player))) {
				output.add(KitResult.ALREADY_OWNED);
				if (data.getKitLevel(this.parentKit) < this.id) {
					data.addKit(this.parentKit, this.id);
				}
			} else {
				if (ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS)) {
					output.add(KitResult.FAILURE_NOT_ENOUGH_WINS);
				}
				if (ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP)) {
					Double diff = (SheepWarsPlugin.getEconomyProvider().getBalance(player) - this.price);
					output.add(diff >= 0 ? KitResult.FAILURE_NEXT_LEVEL_NOT_PURCHASED : KitResult.FAILURE_NEXT_LEVEL_TOO_EXPENSIVE);
				} else {
					output.add(KitResult.FAILURE_NOT_ALLOWED);
				}
			}
			return output;
		}
	}

	public enum KitResult {
		ALREADY_OWNED,
		FAILURE_NEXT_LEVEL_NOT_PURCHASED,
		FAILURE_NEXT_LEVEL_TOO_EXPENSIVE,
		FAILURE_NOT_ALLOWED,
		FAILURE_NOT_ENOUGH_WINS;
	}
}
