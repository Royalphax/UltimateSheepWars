package fr.royalpha.sheepwars.core.manager;

import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.exception.InitConfigException;
import fr.royalpha.sheepwars.core.exception.InvalidFieldException;
import fr.royalpha.sheepwars.core.handler.VirtualLocation;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private static boolean initialized = false;
    private static final String CONFIG_FILE = "config.yml";
    private static final String SETTINGS_FILE = "settings.yml";

    public enum Field {

        ENABLE_HUB_COMMAND("enable-hub-command", FieldType.BOOLEAN, true),
        FALLBACK_SERVER("fallback-server-name", FieldType.STRING, "none"),
        COUNTDOWN("countdown-before-starting", FieldType.INT, 60),
        PREGAME_TASK_COUNTDOWN("countdown-pregame-task", FieldType.INT, 10),
        MIN_PLAYERS("min-players-to-start", FieldType.INT, 6),
        KILLER_VIEW_STAY_TIME("killer-view-stay-time", FieldType.INT, 5),
        GAME_TIME("game-time", FieldType.INT, 20),
        BOARDING_TIME("boarding-time", FieldType.INT, 10),
        GIVE_SHEEP_INTERVAL("give-sheep-seconds", FieldType.INT, 40),
        BOOSTER_INTERVAL("booster-interval-seconds", FieldType.INT, 30),
        BOOSTER_LIFE_TIME("booster-life-time-seconds", FieldType.INT, 10),
        DISPATCH_COMMAND("dispatch-command", FieldType.STRING, "stop"),
        ENABLE_JOIN_FOR_SPECTATORS("enable-join-for-spectate", FieldType.BOOLEAN, true),
        SCOREBOARD_DECORATION("scoreboard-decoration", FieldType.STRING, "my.server.com"),
        AUTO_GENERATE_LANGUAGES("auto-generate-language", FieldType.BOOLEAN, true),
        ALLOW_DEBUG("allow-debug", FieldType.BOOLEAN, false),
        MAX_INTERGALACTIC_SHEEPS("max-intergalactic-sheeps", FieldType.INT, 5),
        @Deprecated
        SHEEP_VELOCITY("sheep-launch-velocity", FieldType.DOUBLE, 3.0d),
        ENABLE_SHEEP_PLAYER_COLLISION("sheep-player-collision", FieldType.BOOLEAN, true),
        KIT_ITEM("item.kit-id", FieldType.ITEMSTACK, "ENDER_CHEST"),
        RETURN_TO_HUB_ITEM("item.return-to-hub-id", FieldType.ITEMSTACK, "BED"),
        PARTICLES_ON_ITEM("item.particles-on-id", FieldType.ITEMSTACK, "BLAZE_ROD"),
        PARTICLES_OFF_ITEM("item.particles-off-id", FieldType.ITEMSTACK, "STICK"),
        VOTING_ITEM("item.voting-item", FieldType.ITEMSTACK, "PAPER"),
        TEAM_BLUE_MATERIAL("item.team-blue", FieldType.MATERIAL, "BANNER"),
        TEAM_RED_MATERIAL("item.team-red", FieldType.MATERIAL, "BANNER"),
        CUSTOMIZE_TABLIST("customize-tablist", FieldType.BOOLEAN, true),
        ENABLE_KIT_PERMISSIONS("enable-permissions", FieldType.BOOLEAN, false),
        ENABLE_INGAME_SHOP("enable-ingame-shop", FieldType.BOOLEAN, false),
        ENABLE_ALL_KITS("enable-all-kits", FieldType.BOOLEAN, true),
        ENABLE_KIT_REQUIRED_WINS("enable-required-wins", FieldType.BOOLEAN, false),
        ENABLE_MYSQL("mysql.enable", FieldType.BOOLEAN, false),
        ENABLE_MYSQL_FREE_HOST("mysql.free-host", FieldType.BOOLEAN, false),
        MYSQL_HOST("mysql.host", FieldType.STRING, "localhost"),
        MYSQL_PORT("mysql.port", FieldType.INT, 3306),
        MYSQL_DATABASE("mysql.database", FieldType.STRING, "sheepwars"),
        MYSQL_TABLE("mysql.table", FieldType.STRING, "players"),
        MYSQL_USER("mysql.user", FieldType.STRING, "root"),
        MYSQL_PASSWORD("mysql.pass", FieldType.STRING, "root"),
        RANKING_TOP("ranking-top", FieldType.INT, 10),
        LOBBY_MAP_NAME("lobby-map-folder-name", FieldType.STRING, "sheepwars-backup"),
        WAITING_GAME_STATE_MOTD("game-state.waiting", FieldType.STRING, "&2\\u2714 &aWaiting &2\\u2714"),
        INGAME_GAME_STATE_MOTD("game-state.in-game", FieldType.STRING, "&4\\u2716 &cRunning &4\\u2716"),
        TERMINATED_GAME_STATE_MOTD("game-state.terminated", FieldType.STRING, "&6\\u2261 &eTerminated &6\\u2261"),
        RESTARTING_GAME_STATE_MOTD("game-state.restarting", FieldType.STRING, "&5\\u26A0 &dRestarting &5\\u26A0"),

        LOBBY("lobby", FieldType.VIRTUAL_LOCATION, VirtualLocation.getDefault(), SETTINGS_FILE),
        //OFFLINE_UUID("offline-mode-db-uuid", FieldType.STRING, UUID.randomUUID().toString().replace("-", ""), SETTINGS_FILE),
        OWNER("owner", FieldType.STRING, "null", SETTINGS_FILE),
        @Deprecated
        BOOSTERS("boosters", FieldType.VIRTUAL_LOCATION_LIST, new ArrayList<VirtualLocation>(), SETTINGS_FILE),
        @Deprecated
        RED_SPAWNS("teams.red.spawns", FieldType.VIRTUAL_LOCATION_LIST, new ArrayList<VirtualLocation>(), SETTINGS_FILE),
        @Deprecated
        BLUE_SPAWNS("teams.blue.spawns", FieldType.VIRTUAL_LOCATION_LIST, new ArrayList<VirtualLocation>(), SETTINGS_FILE),
        @Deprecated
        SPEC_SPAWNS("teams.spec.spawns", FieldType.VIRTUAL_LOCATION_LIST, new ArrayList<VirtualLocation>(), SETTINGS_FILE),

        PREFIX("prefix", FieldType.STRING, "&8[&9SheepWars&8]");

        private String path;
        private FieldType type;
        private Object def;
        private String configName;
        private Object value;

        private Field(String path, FieldType type, Object def) {
            this(path, type, def, CONFIG_FILE);
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

        public boolean isFile(File file) {
            return (this.configName.equals(file.getName()));
        }

        private void setValue(Object obj) {
            this.value = obj;
        }
    }

    public SheepWarsPlugin plugin;

    public ConfigManager(SheepWarsPlugin plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdirs();
    }

    public void initConfig() {
        File file = getFile();
        if (!file.exists()) {
            plugin.getLogger().info("-----------------------------------------------------------------------------------------------");
            plugin.getLogger().info("                        <3 Thanks for choosing UltimateSheepWars <3                            ");
            plugin.getLogger().info("If you need any help, my Discord server is free to join : https://discordapp.com/invite/nZthcPh");
            plugin.getLogger().info("-----------------------------------------------------------------------------------------------");
            plugin.getLogger().info("Generating configuration file ...");
            try (InputStream in = plugin.getResource(CONFIG_FILE)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                ExceptionManager.register(e, true);
                plugin.getLogger().warning("Error when generating configuration file !");
            } finally {
                plugin.getLogger().info("Configuration file was generated with success !");
            }
        }
        init(file);
        initialized = true;
    }

    public void init(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (Field field : Field.values()) {
            if (field.isFile(file))
                switch (field.getType()) {
                    case BOOLEAN:
                        field.setValue(config.getBoolean(field.getPath(), (boolean) field.getDefault()));
                        break;
                    case DOUBLE:
                        field.setValue(config.getDouble(field.getPath(), (double) field.getDefault()));
                        break;
                    case INT:
                        field.setValue(config.getInt(field.getPath(), (int) field.getDefault()));
                        break;
                    case ITEMSTACK:
                        String is = config.getString(field.getPath(), (String) field.getDefault());
                        String[] split = is.split(":");
                        if (split.length > 1) {
                            field.setValue(new ItemBuilder(Material.matchMaterial(split[0])).setData(Byte.parseByte(split[1])).toItemStack());
                        } else {
                            field.setValue(new ItemBuilder(Material.matchMaterial(split[0])).toItemStack());
                        }
                        break;
                    case MATERIAL:
                        field.setValue(Material.matchMaterial(config.getString(field.getPath(), (String) field.getDefault())));
                        break;
                    case STRING:
                        field.setValue(ChatColor.translateAlternateColorCodes('&', config.getString(field.getPath(), (String) field.getDefault())));
                        break;
                    case VIRTUAL_LOCATION:
                        final VirtualLocation location = VirtualLocation.fromString(config.getString(field.getPath(), ((VirtualLocation) field.getDefault()).toString()));
                        field.setValue(location);
                        break;
                    case VIRTUAL_LOCATION_LIST:
                        ConfigurationSection configSection = config.getConfigurationSection(field.getPath());
                        List<VirtualLocation> locList = new ArrayList<>();
                        if (configSection != null)
                            for (final String key : configSection.getKeys(false))
                                locList.add(VirtualLocation.fromString(configSection.getString(key)));
                        field.setValue(locList);
                        break;
                }
        }
    }

    public void save() {
        /** Save the Lobby loc **/
        plugin.getSettingsConfig().set("lobby", ConfigManager.getLocation(Field.LOBBY).toString());
        /** Save the file **/
        try {
            plugin.getSettingsConfig().save(plugin.getSettingsFile());
        } catch (IOException e) {
            ExceptionManager.register(e, true);
        }
    }

    private enum FieldType {
        STRING("String"),
        BOOLEAN("Boolean"),
        DOUBLE("Double"),
        INT("Integer"),
        ITEMSTACK("ItemStack"),
        VIRTUAL_LOCATION("Location"),
        VIRTUAL_LOCATION_LIST("List of Locations"),
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

    public static void setLocation(Field field, Location arg0) {
        checkForException(field, FieldType.VIRTUAL_LOCATION);
        field.setValue(VirtualLocation.fromBukkitLocation(arg0));
    }

    public static void setItemStack(Field field, ItemStack arg0) {
        checkForException(field, FieldType.ITEMSTACK);
        field.setValue(arg0);
    }

    public static void addLocation(Field field, Location arg0) {
        checkForException(field, FieldType.VIRTUAL_LOCATION_LIST);
        ((List<VirtualLocation>) field.getValue()).add(VirtualLocation.fromBukkitLocation(arg0));
    }

    public static void clearLocations(Field field) {
        checkForException(field, FieldType.VIRTUAL_LOCATION_LIST);
        field.setValue(new ArrayList<VirtualLocation>());
    }

    public static void clearLocations(Field field, World filter) {
        checkForException(field, FieldType.VIRTUAL_LOCATION_LIST);
        List<VirtualLocation> locations = (List<VirtualLocation>) field.getValue();
        for (VirtualLocation loc : (List<VirtualLocation>) field.getValue())
            if (loc.getWorld().equals(filter.getName()))
                locations.remove(loc);
        field.setValue(locations);
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

    public static VirtualLocation getLocation(Field field) {
        checkForException(field, FieldType.VIRTUAL_LOCATION);
        return (VirtualLocation) field.getValue();
    }
	
	/*@SuppressWarnings("unchecked")
	public static List<Location> getLocations(Field field, World filter) {
		checkForException(field, FieldType.VIRTUAL_LOCATION_LIST);
		List<Location> locations = new ArrayList<>();
		for (VirtualLocation loc : (List<VirtualLocation>) field.getValue())
			if (loc.getWorld().equals(filter.getName()))
				locations.add(loc.toBukkitLocation());
		return locations;
	}*/

    @SuppressWarnings("unchecked")
    public static List<VirtualLocation> getLocations(Field field) {
        checkForException(field, FieldType.VIRTUAL_LOCATION_LIST);
        List<VirtualLocation> locations = new ArrayList<>((List<VirtualLocation>) field.getValue());
        return locations;
    }

    public static VirtualLocation getRandomLocation(Field field) {
        checkForException(field, FieldType.VIRTUAL_LOCATION_LIST);
        List<VirtualLocation> locs = getLocations(field);
        return locs.get(RandomUtils.nextInt(locs.size()));
    }
	
	/*public static Location getRandomLocation(Field field, World filter) {
		checkForException(field, FieldType.VIRTUAL_LOCATION_LIST);
		List<Location> locs = getLocations(field, filter);
		return locs.get(RandomUtils.nextInt(locs.size()));
	}*/

    private static void checkForException(Field field, FieldType type) {
        if (!initialized)
            new InitConfigException("You can't get any field before the ConfigurationManager class hasn't been initialized first.").printStackTrace();
        if (field.getType() != type)
            new InvalidFieldException("You can't get the field '" + field.toString() + "' as a/an " + type.toString() + " cause it's an instance of a/an " + field.getType().toString()).printStackTrace();
    }

    public File getFile() {
        return new File(plugin.getDataFolder(), CONFIG_FILE);
    }
}
