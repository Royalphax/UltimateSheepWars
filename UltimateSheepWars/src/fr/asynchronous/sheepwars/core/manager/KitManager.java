package fr.asynchronous.sheepwars.core.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.exception.ConfigFileNotSet;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public abstract class KitManager 
{
	
    /*MORE_HEALTH(1, "sheepwars.kit.morehealth", 24, MsgEnum.KIT_MORE_HEALTH_NAME, MsgEnum.KIT_MORE_HEALTH_DESCRIPTION, new ItemBuilder(Material.APPLE)), 
    BETTER_BOW(2, "sheepwars.kit.betterbow", 32, MsgEnum.KIT_BETTER_BOW_NAME, MsgEnum.KIT_BETTER_BOW_DESCRIPTION, new ItemBuilder(Material.BOW)), 
    BETTER_SWORD(3, "sheepwars.kit.bettersword", 29, MsgEnum.KIT_BETTER_SWORD_NAME, MsgEnum.KIT_BETTER_SWORD_DESCRIPTION, new ItemBuilder(Material.STONE_SWORD)), 
    MORE_SHEEP(4, "sheepwars.kit.moresheep", 23, MsgEnum.KIT_MORE_SHEEP_NAME, MsgEnum.KIT_MORE_SHEEP_DESCRIPTION, new ItemBuilder(Material.WOOL)), 
    BUILDER(5, "sheepwars.kit.builder", 30, MsgEnum.KIT_BUILDER_NAME, MsgEnum.KIT_BUILDER_DESCRIPTION, new ItemBuilder(Material.BRICK)), 
    DESTROYER(6, "sheepwars.kit.destroyer", 33, MsgEnum.KIT_DESTROYER_NAME, MsgEnum.KIT_DESTROYER_DESCRIPTION, new ItemBuilder(Material.TNT)),
    MOBILITY(7, "sheepwars.kit.mobility", 21, MsgEnum.KIT_MOBILITY_NAME, MsgEnum.KIT_MOBILITY_DESCRIPTION, new ItemBuilder(Material.LEATHER_BOOTS)),
    ARMORED_SHEEP(8, "sheepwars.kit.armoredsheep", 20, MsgEnum.KIT_AMORED_SHEEP_NAME, MsgEnum.KIT_AMORED_SHEEP_DESCRIPTION, new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.BLACK)),
	
	RANDOM(9, "sheepwars.kit.random", 31, MsgEnum.KIT_RANDOM_NAME, MsgEnum.KIT_RANDOM_DESCRIPTION, new ItemBuilder(Material.SKULL_ITEM).setSkullTexture("http://textures.minecraft.net/texture/cc7d1b18398acd6e7e692a833a2217aea6b5a770f42c43513e4358cacd1b9c")),
	NULL(0, "sheepwars.kit.none", 22, MsgEnum.KIT_NULL_NAME, MsgEnum.KIT_NULL_DESCRIPTION, new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.RED));
    */
    
    private static ArrayList<KitManager> availableKits = new ArrayList<>();
    private static File configFile;
    private static FileConfiguration config;
    
    private int id;
    private String configPath;
    private Message name;
    private Message description;
    private String permission;
    private ItemBuilder icon;
    
    private Double price;
    private int wins;
    
    public KitManager(final int id, final String name, final String description, final String permission, final double price, final int requiredWins, final ItemBuilder icon) {
    	this(id, name, new Message(name), new Message(description), permission, price, requiredWins, icon);
    }
    
    public KitManager(final int id, final MsgEnum name, final MsgEnum description, final String permission, final double price, final int requiredWins, final ItemBuilder icon) {
    	this(id, name.toString().replaceAll("KIT_", "").replaceAll("_NAME", ""), Message.getMessageByEnum(name), Message.getMessageByEnum(description), permission, price, requiredWins, icon);
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
    
    public List<KitResult> useKit(Player player, UltimateSheepWarsPlugin plugin) {
    	List<KitResult> output = new ArrayList<>();
    	PlayerData data = PlayerData.getPlayerData(player);
    	if ((ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS) && data.getWins() >= this.wins) || (ConfigManager.getBoolean(Field.ENABLE_KIT_PERMISSIONS) && player.hasPermission(this.permission)) || ConfigManager.getBoolean(Field.ENABLE_ALL_KITS) || (Contributor.isImportant(player)))
    	{	
    		data.setKit(this);
    		output.add(KitResult.SUCCESS);
    	} else {
    		if (ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS)) {
    			output.add(KitResult.FAILURE_NOT_ENOUGH_WINS);
    		}
    		if (ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP)) {
    			Double diff = (plugin.ECONOMY_PROVIDER.getBalance(player) - this.price);
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
    
    public static enum TriggerKitAction {
		OTHER,
		ARROW_LAUNCH;
	}
    
    public enum KitResult {
    	SUCCESS,
    	FAILURE_NOT_PURCHASED,
    	FAILURE_TOO_EXPENSIVE,
    	FAILURE_NOT_ALLOWED,
    	FAILURE_NOT_ENOUGH_WINS;
    }
    
    public abstract boolean onEquip(final Player player);
    
    public abstract void onEvent(final Player player, final Event event, final TriggerKitAction triggerAction);
    
    public static KitManager getFromId(int id)
    {
    	for (KitManager kit : availableKits)
    		if (kit.getId() == id)
    			return kit;
    	return null;
    }
    
    public static List<KitManager> getAvailableKits() {
    	return availableKits;
    }

	public static boolean registerKit(KitManager kit) throws ConfigFileNotSet, IOException {
		if (!availableKits.contains(kit)) {
			if (configFile == null || config == null)
				throw new ConfigFileNotSet("You have to set the config file used to store booster's data before registering a booster.");
			boolean enable = config.getBoolean(kit.getConfigFieldPath("enable"), true);
			if (!enable)
				return false;
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
			availableKits.add(kit);
			return true;
		}
		return false;
	}
	
	public static boolean unregisterKit(KitManager kit) {
		if (availableKits.contains(kit)) {
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
