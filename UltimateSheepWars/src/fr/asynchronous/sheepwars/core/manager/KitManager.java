package fr.asynchronous.sheepwars.core.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.exception.KitNotRegistredException;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public abstract class KitManager implements Listener
{
    private static ArrayList<KitManager> availableKits = new ArrayList<>();
    private static ArrayList<KitManager> waitingKits = new ArrayList<>();
    private static File configFile;
    private static FileConfiguration config;
    
    private int id;
    private String configPath;
    private Message name;
    private Message description;
    private String permission;
    private ItemBuilder icon;
    private Plugin plugin;
    
    private Double price;
    private int wins;
    
    /**
     * Initialize a new Kit !
     * 
     * @param id Unique ID of this Kit. Default kits : 0-9.
     * @param name Name of this Kit.
     * @param description Description of this Kit.
     * @param permission Permission needed to use this Kit.
     * @param price Price of this Kit.
     * @param requiredWins Required wins to use this Kit.
     * @param icon Icon of this Kit.
     */
    public KitManager(final int id, final String name, final String description, final String permission, final double price, final int requiredWins, final ItemBuilder icon) {
    	this(id, name, new Message(name), new Message(description), permission, price, requiredWins, icon);
    }
    
    /**
	 * Use {@link #KitManager(int, String, String, String, double, int, ItemBuilder) this constructor} instead.
	 */
    public KitManager(final int id, final MsgEnum name, final MsgEnum description, final String permission, final double price, final int requiredWins, final ItemBuilder icon) {
    	this(id, name.toString().replaceAll("KIT_", "").replaceAll("_NAME", ""), Message.getMessage(name), Message.getMessage(description), permission, price, requiredWins, icon);
    }
    
    /**
	 * Use {@link #KitManager(int, String, String, String, double, int, ItemBuilder) this constructor} instead.
	 */
    public KitManager(final int id, final String configPath, final Message name, final Message description, final String permission, final double price, final int requiredWins, final ItemBuilder icon) {
    	this.id = id;
    	this.configPath = "kit." + configPath.replaceAll("_", "-").toLowerCase();
        this.name = name;
        this.description = description;
        this.permission = permission;
    	this.icon = icon;
    	this.price = price;
    	this.wins = requiredWins;
    }
    
    /**
	 * Get the kit name according to the player's language.
	 */
    public String getName(Player player) {
        return this.name.getMessage(player);
    }
    
    /**
	 * Get the kit description according to the player's language.
	 */
    public String getDescription(Player player) {
    	return this.description.getMessage(player);
    }
    
    /**
	 * Get the kit icon.
	 */
    public ItemBuilder getIcon() {
        return this.icon;
    }
    
    /**
	 * Get the permission needed to use this kit if permissions are enabled in UltimateSheepWars's config file.
	 */
    public String getPermission() {
        return this.permission;
    }
    
    /**
	 * Plugin who register this kit.
	 */
    public Plugin getPlugin() {
        return this.plugin;
    }
    
    /**
	 * Get if this kit id is equal to another id.
	 */
    public boolean isKit(int i) {
    	return (this.id == i);
    }
    
    /**
	 * Get if a player can use a kit.
	 */
    public List<KitResult> canUseKit(Player player, UltimateSheepWarsPlugin plugin) {
    	List<KitResult> output = new ArrayList<>();
    	PlayerData data = PlayerData.getPlayerData(player);
    	if (isKit(8) || isKit(9) || (ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS) && data.getWins() >= this.wins) || (ConfigManager.getBoolean(Field.ENABLE_KIT_PERMISSIONS) && player.hasPermission(this.permission)) || ConfigManager.getBoolean(Field.ENABLE_ALL_KITS) || (Contributor.isDeveloper(player)))
    	{	
    		output.add(KitResult.SUCCESS);
    	} else {
    		if (ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS)) {
    			output.add(KitResult.FAILURE_NOT_ENOUGH_WINS);
    		}
    		if (ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP)) {
    			Double diff = (plugin.getEconomyProvider().getBalance(player) - this.price);
    			output.add(diff >= 0 ? KitResult.FAILURE_NOT_PURCHASED : KitResult.FAILURE_TOO_EXPENSIVE);
    		} else {
    			output.add(KitResult.FAILURE_NOT_ALLOWED);
    		}
        }
    	return output;
    }
    
    /**
	 * Get the amount of required wins to use this kit if required wins is enabled in the UltimateSheepWars's config file.
	 */
    public int getRequiredWins() {
        return this.wins;
    }
    
    /**
	 * Get the price of this sheep if ingame-shop is enabled in the UltimateSheepWars's config file.
	 */
    public Double getPrice() {
        return this.price;
    }
    
    /**
	 * Get kit id.
	 */
    public int getId() {
        return this.id;
    }
    
    /**
	 * No need to use this method.
	 */
    private String getConfigFieldPath(String field) {
    	return this.configPath + "." + field;
    }
    
    public enum KitResult {
    	SUCCESS,
    	FAILURE_NOT_PURCHASED,
    	FAILURE_TOO_EXPENSIVE,
    	FAILURE_NOT_ALLOWED,
    	FAILURE_NOT_ENOUGH_WINS;
    }
    
    /**
     * Triggered when the game begins.
     * 
     * @param player Player who chose this kit.
     * @return <b>true</b> if you want to let the plugin equiping all the default things to the player (leather armor, sword, bow, etc.). <b>false</b> if you want to set a special inventory content to the player.
     */
    public abstract boolean onEquip(final Player player);
    
    /**
     * Get if this ID is linked to a kit.
     */
    public static boolean existKit(int id)
    {
    	return !(getFromId(id) == null);
    }
    
    /**
     * Get a Kit from an ID.
     */
    public static KitManager getFromId(int id)
    {
    	for (KitManager kit : availableKits)
    		if (kit.getId() == id)
    			return kit;
    	return null;
    }
    
    /**
     * Get the same instance of a Kit.
     */
    public static KitManager getInstanceKit(KitManager kit) {
    	for (KitManager k : availableKits) {
    		if (k.getId() == kit.getId()) {
    			return k;
    		}
    	}
    	new KitNotRegistredException("Class " + kit.getClass().getName() + " has to be registred as a kit first.").printStackTrace();
		return null;
    }
    
    /**
     * Get registered kits.
     */
    public static List<KitManager> getAvailableKits() {
    	return availableKits;
    }

    /**
	 * Use {@link fr.asynchronous.sheepwars.core.UltimateSheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
	public static boolean registerKit(KitManager kit, Plugin plugin) throws IOException {
		if (!availableKits.contains(kit)) {
			if (configFile == null || config == null) {
				waitingKits.add(kit);
				return false;
			}
			boolean enable = config.getBoolean(kit.getConfigFieldPath("enable"), true);
			double price = config.getDouble(kit.getConfigFieldPath("price"), -1.0);
			int requiredWins = config.getInt(kit.getConfigFieldPath("required-wins"), -1);
			if (price < 0.0 || requiredWins < 0.0) {
				config.set(kit.getConfigFieldPath("enable"), true);
				config.set(kit.getConfigFieldPath("price"), kit.price);
				config.set(kit.getConfigFieldPath("required-wins"), kit.wins);
				config.save(configFile);
			} else {
				kit.price = price;
				kit.wins = requiredWins;
			}
			kit.plugin = plugin;
			if (!enable)
				return false;
			Bukkit.getPluginManager().registerEvents(kit, plugin);
			availableKits.add(kit);
			return true;
		}
		return false;
	}
	
	/**
	 * Use {@link fr.asynchronous.sheepwars.core.UltimateSheepWarsAPI UltimateSheepWarsAPI} methods instead.
	 */
	public static boolean unregisterKit(KitManager kit) {
		if (availableKits.contains(kit)) {
			HandlerList.unregisterAll(kit);
			availableKits.remove(kit);
			return true;
		}
		return false;
	}
    
	/**
	 * No need to use this method.
	 */
    public static void setupConfig(File file, UltimateSheepWarsPlugin plugin)
    {
    	if (!file.exists()) {
    		new FileNotFoundException(file.getName() + " not found. You probably need to create it.").printStackTrace();
    		return;
    	}
    	configFile = file;
    	config = YamlConfiguration.loadConfiguration(file);
    	for (KitManager kit : waitingKits)
    		try {
				registerKit(kit, plugin);
				plugin.getLogger().info("Custom Kit : " + kit.getClass().getName() + " fully registred!");
			} catch (IOException e) {
				plugin.getLogger().info("Can't register custom kit " + kit.getClass().getName() + ", an error occured!");
				new ExceptionManager(e).register(true);
			}
    	waitingKits.clear();
    }
}
