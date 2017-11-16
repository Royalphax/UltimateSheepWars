package fr.asynchronous.sheepwars.core.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.exception.ConfigurationManagerClassHasntBeenInitialized;
import fr.asynchronous.sheepwars.core.exception.InvalidFieldTypeException;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.Utils;

public class ConfigManager {

	private static boolean initialized = false;

	public enum Field {

		FALLBACK_SERVER("fallback-server-name", FieldType.STRING, "none"),
		COUNTDOWN("countdown-before-starting", FieldType.INT, 60),
		MIN_PLAYERS("min-players-to-start", FieldType.INT, 6),
		GAME_TIME("game-time", FieldType.INT, 20),
		BOARDING_TIME("boarding-time", FieldType.INT, 10),
		GIVE_SHEEP_INTERVAL("give-sheep-seconds", FieldType.INT, 40),
		BOOSTER_INTERVAL("booster-interval-seconds", FieldType.INT, 30),
		BOOSTER_LIFE_TIME("booster-life-time-seconds", FieldType.INT, 10),
		DISPATCH_COMMAND("dispatch-command", FieldType.STRING, "stop"),
		ENABLE_JOIN_FOR_SPECTATORS("enable-join-for-spectate", FieldType.BOOLEAN, true),
		SCOREBOARD_DECORATION("scoreboard-decoration", FieldType.STRING, "my.server.com"),
		AUTO_GENERATE_LANGUAGES("auto-generate-language", FieldType.BOOLEAN, true),
		SHEEP_COUNTDOWN_TO_EXPLODE("sheep-countdown-before-explosion", FieldType.INT, 5),
		SHEEP_VELOCITY("sheep-launch-velocity", FieldType.DOUBLE, 2.2d),
		SHEEP_HEALTH("sheep-health", FieldType.INT, 8),
		KIT_ITEM("item.kit-id", FieldType.ITEMSTACK, "ENDER_CHEST"),
		RETURN_TO_HUB_ITEM("item.return-to-hub-id", FieldType.ITEMSTACK, "BED"),
		PARTICLES_ON_ITEM("item.particles-on-id", FieldType.ITEMSTACK, "BLAZE_ROD"),
		PARTICLES_OFF_ITEM("item.particles-off-id", FieldType.ITEMSTACK, "STICK"),
		TEAM_BLUE_MATERIAL("item.team-blue", FieldType.MATERIAL, "BANNER"),
		TEAM_RED_MATERIAL("item.team-red", FieldType.MATERIAL, "BANNER"),
		ENABLE_KIT_PERMISSIONS("enable-permissions", FieldType.BOOLEAN, false),
		ENABLE_INGAME_SHOP("enable-ingame-shop", FieldType.BOOLEAN, false),
		ENABLE_ALL_KITS("enable-all-kits", FieldType.BOOLEAN, true),
		ENABLE_KIT_REQUIRED_WINS("enable-required-wins", FieldType.BOOLEAN, false),
		ENABLE_MYSQL("mysql.enable", FieldType.BOOLEAN, false),
		ENABLE_MYSQL_FREE_HOST("mysql.free-host", FieldType.BOOLEAN, false),
		MYSQL_HOST("mysql.host", FieldType.STRING, "localhost"),
		MYSQL_PORT("mysql.port", FieldType.INT, 3306),
		MYSQL_DATABASE("mysql.database", FieldType.STRING, "sheepwars"),
		MYSQL_USER("mysql.user", FieldType.STRING, "root"),
		MYSQL_PASSWORD("mysql.pass", FieldType.STRING, "root"),
		RANKING_TOP("ranking-top", FieldType.INT, 10),
		LOBBY_GAME_STATE_MOTD("game-state.lobby", FieldType.STRING, "&2\\u2714 &aWaiting &2\\u2714"),
		INGAME_GAME_STATE_MOTD("game-state.in-game", FieldType.STRING, "&4\\u2716 &cRunning &4\\u2716"),
		POST_GAME_GAME_STATE_MOTD("game-state.post-game", FieldType.STRING, "&6\\u2261 &eEnding &6\\u2261"),
		TERMINATED_GAME_STATE_MOTD("game-state.terminated", FieldType.STRING, "&5\\u26A0 &dRestarting &5\\u26A0"),
		
		LOBBY("lobby", FieldType.LOCATION, new Location(Bukkit.getWorlds().get(0), 0, 0, 0), "settings.yml"),
		OWNER("owner", FieldType.STRING, "null", "settings.yml"),
		BOOSTERS("boosters", FieldType.LOCATION_LIST, null, "settings.yml"),
		RED_SPAWNS("teams.red.spawns", FieldType.LOCATION_LIST, null, "settings.yml"),
		BLUE_SPAWNS("teams.blue.spawns", FieldType.LOCATION_LIST, null, "settings.yml"),
		SPEC_SPAWNS("teams.spec.spawns", FieldType.LOCATION_LIST, null, "settings.yml"),
		
		PREFIX("prefix", FieldType.STRING, "&8[&9SheepWars&8]");

		private String path;
		private FieldType type;
		private Object def;
		private String configName;
		private Object value;

		private Field(String path, FieldType type, Object def) {
			this(path, type, def, "config.yml");
		}

		private Field(String path, FieldType type, Object def, String configName) {
			this.path = path;
			this.type = type;
			this.def = def;
			this.configName = configName;
		}

		public String getPath() {
			return this.path;
		}

		public FieldType getType() {
			return this.type;
		}

		public Object getValue() {
			return this.value;
		}

		public Object getDefault() {
			return this.def;
		}

		public boolean isConfig(FileConfiguration config) {
			return (this.configName.equals(config.getName()));
		}

		private void setValue(Object obj) {
			this.value = obj;
		}

		public static void init(FileConfiguration config) {
			for (Field field : values()) {
				if (field.isConfig(config))
					switch (field.getType()) {
						case BOOLEAN :
							field.setValue(config.getBoolean(field.getPath(), (boolean) field.getDefault()));
							break;
						case DOUBLE :
							field.setValue(config.getDouble(field.getPath(), (double) field.getDefault()));
							break;
						case INT :
							field.setValue(config.getInt(field.getPath(), (int) field.getDefault()));
							break;
						case ITEMSTACK :
							String is = config.getString(field.getPath(), (String) field.getDefault());
							String[] split = is.split(":");
							if (split.length > 1) {
								field.setValue(new ItemBuilder(Material.matchMaterial(split[0])).setData(Byte.parseByte(split[1])));
							} else {
								field.setValue(new ItemBuilder(Material.matchMaterial(split[0])));
							}
							break;
						case MATERIAL :
							field.setValue(Material.matchMaterial(config.getString(field.getPath(), (String) field.getDefault())));
							break;
						case STRING :
							field.setValue(ChatColor.translateAlternateColorCodes('&', config.getString(field.getPath(), (String) field.getDefault())));
							break;
						case LOCATION :
							field.setValue(Utils.toLocation(config.getString(field.getPath(), Utils.toString((Location) field.getDefault()))));
							break;
						case LOCATION_LIST :
							ConfigurationSection configSection = config.getConfigurationSection(field.getPath());
							List<Location> locList = new ArrayList<>();
					        if (configSection != null)
					            for (final String key : configSection.getKeys(false))
					            	locList.add(Utils.toLocation(configSection.getString(key)));
							field.setValue(locList);
							break;
					}
			}
		}
	}

	private enum FieldType {
		STRING("String"),
		BOOLEAN("Boolean"),
		DOUBLE("Double"),
		INT("Integer"),
		ITEMSTACK("ItemStack"),
		LOCATION("Location"),
		LOCATION_LIST("List of Locations"),
		MATERIAL("Material");

		private String toString;
		private FieldType(String toString) {
			this.toString = toString;
		}

		@Override
		public String toString() {
			return this.toString;
		}
	}
	
	public static void setBoolean(Field field, boolean arg0) {
		checkForException(field, FieldType.BOOLEAN);
		field.setValue(arg0);
	}
	
	public static void setString(Field field, String arg0) {
		checkForException(field, FieldType.STRING);
		field.setValue(arg0);
	}

	public static String getString(Field field) {
		checkForException(field, FieldType.STRING);
		return (String) field.getValue();
	}
	
	public static Boolean getBoolean(Field field) {
		checkForException(field, FieldType.BOOLEAN);
		return (Boolean) field.getValue();
	}

	public static Integer getInteger(Field field) {
		checkForException(field, FieldType.INT);
		return (Integer) field.getValue();
	}

	public static Integer getInt(Field field) {
		return getInteger(field);
	}

	public static Double getDouble(Field field) {
		checkForException(field, FieldType.DOUBLE);
		return (Double) field.getValue();
	}

	public static ItemStack getItemStack(Field field) {
		checkForException(field, FieldType.ITEMSTACK);
		return (ItemStack) field.getValue();
	}

	public static Material getMaterial(Field field) {
		checkForException(field, FieldType.MATERIAL);
		return (Material) field.getValue();
	}
	
	public static Location getLocation(Field field) {
		checkForException(field, FieldType.LOCATION);
		return (Location) field.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public static List<Location> getLocations(Field field) {
		checkForException(field, FieldType.LOCATION_LIST);
		return (List<Location>) field.getValue();
	}

	private static void checkForException(Field field, FieldType type) {
		if (!initialized)
			new ConfigurationManagerClassHasntBeenInitialized("You can't get any field before the ConfigurationManager class hasn't been initialized first.").printStackTrace();
		if (field.getType() != type)
			new InvalidFieldTypeException("You can't get the field '" + field.toString() + "' as a/an " + type.toString() + " when it's an instance of a/an " + field.getType().toString()).printStackTrace();
	}

	public static void initConfig(UltimateSheepWarsPlugin instance) {
		if (!instance.getDataFolder().exists())
			instance.getDataFolder().mkdirs();
		File file = new File(instance.getDataFolder(), "config.yml");
		if (!file.exists()) {
			instance.getLogger().info("Thanks for using UltimateSheepWars from Asynchronous.");
			instance.getLogger().info("Generating configuration file ...");
			try (InputStream in = instance.getResource("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				new ExceptionManager(e).register(true);
				instance.getLogger().warning("Error when generating configuration file !");
			} finally {
				instance.getLogger().info("Configuration file was generated with success !");
			}
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		Field.init(config);
		initialized = true;
	}
}
