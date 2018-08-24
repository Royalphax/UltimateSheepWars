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
import fr.asynchronous.sheepwars.core.exception.ConfigFileNotSet;
import fr.asynchronous.sheepwars.core.exception.KitNotRegistredException;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public abstract class KitManager implements Listener
{
    private static ArrayList<KitManager> availableKits = new ArrayList<>();
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
    
    public KitManager(final int id, final String name, final String description, final String permission, final double price, final int requiredWins, final ItemBuilder icon) {
    	this(id, name, new Message(name), new Message(description), permission, price, requiredWins, icon);
    }
    
    public KitManager(final int id, final MsgEnum name, final MsgEnum description, final String permission, final double price, final int requiredWins, final ItemBuilder icon) {
    	this(id, name.toString().replaceAll("KIT_", "").replaceAll("_NAME", ""), Message.getMessage(name), Message.getMessage(description), permission, price, requiredWins, icon);
    }
    
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
    
    public String getName(Player player) {
        return this.name.getMessage(player);
    }
    
    public String getDescription(Player player) {
    	return this.description.getMessage(player);
    }
    
    public ItemBuilder getIcon() {
        return this.icon;
    }
    
    public String getPermission() {
        return this.permission;
    }
    
    public Plugin getPlugin() {
        return this.plugin;
    }
    
    public boolean isKit(int i) {
    	return (this.id == i);
    }
    
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
    
    public int getRequiredWins() {
        return this.wins;
    }
    
    public Double getPrice() {
        return this.price;
    }
    
    public int getId() {
        return this.id;
    }
    
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
    
    public abstract boolean onEquip(final Player player);
    
    public static boolean existKit(int id)
    {
    	return !(getFromId(id) == null);
    }
    
    public static KitManager getFromId(int id)
    {
    	for (KitManager kit : availableKits)
    		if (kit.getId() == id)
    			return kit;
    	return null;
    }
    
    public static KitManager getInstanceKit(KitManager kit) {
    	for (KitManager k : availableKits) {
    		if (k.getId() == kit.getId()) {
    			return k;
    		}
    	}
    	new KitNotRegistredException("Class " + kit.getClass().getName() + " has to be registred as a kit first.").printStackTrace();
		return null;
    }
    
    public static List<KitManager> getAvailableKits() {
    	return availableKits;
    }

	public static boolean registerKit(KitManager kit, Plugin plugin) throws ConfigFileNotSet, IOException {
		if (!availableKits.contains(kit)) {
			if (configFile == null || config == null)
				throw new ConfigFileNotSet("You have to set the config file used to store kit's data before registering a new kit.");
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
	
	public static boolean unregisterKit(KitManager kit) {
		if (availableKits.contains(kit)) {
			HandlerList.unregisterAll(kit);
			availableKits.remove(kit);
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
