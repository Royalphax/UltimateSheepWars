package fr.asynchronous.sheepwars.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.command.ContributorCommand;
import fr.asynchronous.sheepwars.core.command.HubCommand;
import fr.asynchronous.sheepwars.core.command.LangCommand;
import fr.asynchronous.sheepwars.core.command.MainCommand;
import fr.asynchronous.sheepwars.core.command.StatsCommand;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.event.block.BlockBreak;
import fr.asynchronous.sheepwars.core.event.block.BlockPlace;
import fr.asynchronous.sheepwars.core.event.block.BlockSpread;
import fr.asynchronous.sheepwars.core.event.entity.CreatureSpawn;
import fr.asynchronous.sheepwars.core.event.entity.EntityBlockForm;
import fr.asynchronous.sheepwars.core.event.entity.EntityChangeBlock;
import fr.asynchronous.sheepwars.core.event.entity.EntityDamage;
import fr.asynchronous.sheepwars.core.event.entity.EntityDamageByPlayer;
import fr.asynchronous.sheepwars.core.event.entity.EntityDeath;
import fr.asynchronous.sheepwars.core.event.entity.EntityExplode;
import fr.asynchronous.sheepwars.core.event.entity.EntityTarget;
import fr.asynchronous.sheepwars.core.event.entity.FoodLevelChange;
import fr.asynchronous.sheepwars.core.event.inventory.InventoryClick;
import fr.asynchronous.sheepwars.core.event.inventory.InventoryOpenEvent;
import fr.asynchronous.sheepwars.core.event.player.AsyncPlayerChat;
import fr.asynchronous.sheepwars.core.event.player.PlayerAchievementAwarded;
import fr.asynchronous.sheepwars.core.event.player.PlayerArmorStandManipulate;
import fr.asynchronous.sheepwars.core.event.player.PlayerCommandPreprocess;
import fr.asynchronous.sheepwars.core.event.player.PlayerDamage;
import fr.asynchronous.sheepwars.core.event.player.PlayerDamageByEntity;
import fr.asynchronous.sheepwars.core.event.player.PlayerDeath;
import fr.asynchronous.sheepwars.core.event.player.PlayerDropItem;
import fr.asynchronous.sheepwars.core.event.player.PlayerInteract;
import fr.asynchronous.sheepwars.core.event.player.PlayerInteractAtEntity;
import fr.asynchronous.sheepwars.core.event.player.PlayerJoin;
import fr.asynchronous.sheepwars.core.event.player.PlayerKick;
import fr.asynchronous.sheepwars.core.event.player.PlayerLogin;
import fr.asynchronous.sheepwars.core.event.player.PlayerMove;
import fr.asynchronous.sheepwars.core.event.player.PlayerPickupItem;
import fr.asynchronous.sheepwars.core.event.player.PlayerQuit;
import fr.asynchronous.sheepwars.core.event.player.PlayerRespawn;
import fr.asynchronous.sheepwars.core.event.player.PlayerSwapItem;
import fr.asynchronous.sheepwars.core.event.projectile.ProjectileHit;
import fr.asynchronous.sheepwars.core.event.projectile.ProjectileLaunch;
import fr.asynchronous.sheepwars.core.event.server.bfA;
import fr.asynchronous.sheepwars.core.event.server.bfB;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.MinecraftVersion;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.RewardsManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.manager.URLManager;
import fr.asynchronous.sheepwars.core.manager.VersionManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.stat.AccountManager;
import fr.asynchronous.sheepwars.core.stat.DataRegister;
import fr.asynchronous.sheepwars.core.stat.MySQL;
import fr.asynchronous.sheepwars.core.task.BeginCountdown;
import fr.asynchronous.sheepwars.core.task.GameTask;
import fr.asynchronous.sheepwars.core.task.ScoreboardTask;
import fr.asynchronous.sheepwars.core.util.FileUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import fr.asynchronous.sheepwars.core.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class UltimateSheepWarsPlugin extends JavaPlugin {

	public static final String PACKAGE = "fr.asynchronous.sheepwars";
	public static final File DATAFOLDER = new File("plugins/UltimateSheepWars/");
	public static final String user_id = "%%__USER__%%";
	public static final String download_id = "%%__NONCE__%%";
	
	private static VersionManager versionManager;
	
	public UltimateSheepWarsPlugin instance;
	public AccountManager accountManager;
	public RewardsManager rewardManager;
    public Boolean isUpToDate;
    public Boolean LOCALHOST;
    public World WORLD;
    public Boolean VAULT_INSTALLED;
    public Boolean CHAT_PROVIDER_INSTALLED;
    public Boolean ENABLE_PLUGIN;
	public MySQL DATABASE;
    public FileConfiguration SETTINGS_CONFIG;
    public Economy ECONOMY_PROVIDER;
    public Permission PERMISSION_PROVIDER;
    public Chat CHAT_PROVIDER;
    public Boolean LEADER_HEADS_INSTALLED;
    public File SETTINGS_FILE;
    public Boolean MySQL_ENABLE;
    public Boolean CHRISTMAS_MODE;
    public Boolean APRIL_FOOL_MODE;
    private BeginCountdown PRE_GAME_TASK;
	private GameTask GAME_TASK;
	
	public UltimateSheepWarsPlugin() {
        this.ECONOMY_PROVIDER = null;
        this.ENABLE_PLUGIN = true;
        this.isUpToDate = true;
        this.MySQL_ENABLE = false;
        this.VAULT_INSTALLED = false;
        
        /** Ne pas oublier de changer la valeur **/
        this.LOCALHOST = true;
	}
	
	@Override
	public void onLoad() {
		if (!isSpigotServer()) {
			disablePlugin(Level.WARNING, "This server isn't running with Spigot build. The plugin can't be load.");
			return;
		}
		try {
			versionManager = new VersionManager();
		} catch (ReflectiveOperationException e) {
			disablePlugin(Level.WARNING, "ReflectiveOperationException occurs. Please contact the developer.");
			return;
		}
		if (!versionManager.getVersion().inRange(MinecraftVersion.v1_8_R3, MinecraftVersion.v1_9_R1, MinecraftVersion.v1_9_R2, MinecraftVersion.v1_10_R1, MinecraftVersion.v1_11_R1))
		{
			disablePlugin(Level.WARNING, "UltimateSheepWars doesn't support your server version (" + versionManager.getVersion().toString() + ")");
			return;
		}
		if (!Bukkit.getWorlds().get(0).getName().equals("world"))
		{
			disablePlugin(Level.WARNING, "The server.properties's level name must be \"world\".");
			return;
		}
		try {
            Bukkit.unloadWorld("world", true);
            this.getLogger().info("Loading directories...");
            final File worldContainer = this.getServer().getWorldContainer();
            final File worldFolder = new File(worldContainer, "world");
            final File copyFolder = new File(worldContainer, "sheepwars-backup");
            if (copyFolder.exists()) {
            	this.getLogger().info("World is reseting...");
                ReflectionUtils.getClass("RegionFileCache", ReflectionUtils.PackageType.MINECRAFT_SERVER).getMethod("a", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                FileUtils.delete(worldFolder);
                FileUtils.copyFolder(copyFolder, worldFolder);
                this.getLogger().info("World reset!");
            } else {
            	this.getLogger().info("Didn't find the world backup save, creating it...");
            	FileUtils.copyFolder(worldFolder, copyFolder);
            	this.getLogger().info("Backup save created!");
            }
        }
        catch (Throwable ex) {
            try {
				throw ex;
			} catch (Throwable th) {
				this.getLogger().severe("*** An error occured when reseting the map. Please contact the developer. ***");
				new ExceptionManager(th).register(true);
			}
        }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		versionManager.getCustomEntities().registerEntities();
		GameState.setCurrentStep(GameState.LOBBY, this);
        this.WORLD = Bukkit.getWorlds().get(0);
        this.WORLD.setGameRuleValue("doDaylightCycle", "false");
        this.WORLD.setTime(6000L);
        this.WORLD.setStorm(false);
        this.WORLD.setThundering(false);
        this.getCommand("sheepwars").setExecutor(new MainCommand(this));
        this.getCommand("lang").setExecutor(new LangCommand(this));
        this.getCommand("stats").setExecutor(new StatsCommand(this));
        this.getCommand("hub").setExecutor(new HubCommand(this));
        this.getCommand("contributor").setExecutor(new ContributorCommand(this));
        this.register(BlockBreak.class, BlockPlace.class, BlockSpread.class, 
        		
        		CreatureSpawn.class, EntityBlockForm.class, EntityChangeBlock.class, 
        		EntityDamageByPlayer.class, EntityDeath.class, EntityExplode.class, 
        		EntityTarget.class, FoodLevelChange.class, EntityDamage.class,
        		
        		InventoryClick.class, 
        		
        		AsyncPlayerChat.class, PlayerAchievementAwarded.class, PlayerArmorStandManipulate.class, 
        		PlayerCommandPreprocess.class, PlayerDamage.class, PlayerDamageByEntity.class,
        		PlayerDeath.class, PlayerDropItem.class, PlayerInteract.class, PlayerJoin.class, 
        		PlayerKick.class, PlayerLogin.class, PlayerMove.class, PlayerPickupItem.class,
        		PlayerQuit.class, PlayerRespawn.class, PlayerInteractAtEntity.class, InventoryOpenEvent.class, 
        		
        		ProjectileHit.class, ProjectileLaunch.class, 
        		
        		bfA.class, bfB.class);
        if (versionManager.getVersion().newerThan(MinecraftVersion.v1_9_R1))
        	this.register(PlayerSwapItem.class);
		VAULT_INSTALLED = Bukkit.getPluginManager().isPluginEnabled("Vault");
		if (VAULT_INSTALLED)
			getLogger().info("Vault hooked!");
		LEADER_HEADS_INSTALLED = Bukkit.getPluginManager().isPluginEnabled("LeaderHeads");
		if (LEADER_HEADS_INSTALLED)
			getLogger().info("LeaderHeads hooked!");
		this.load();
		new BukkitRunnable()
		{
			public void run()
			{
				try {
					if (!URLManager.checkVersion(getDescription().getVersion(), false, URLManager.Link.GITHUB_PATH)) {
						isUpToDate = false;
						getLogger().info("A new version is available, with following new functionalities, improvements and fixes : ");
						List<String> news = URLManager.getInfoVersion(URLManager.Link.GITHUB_PATH);
						for (int i = 0; i < news.size(); i++) {
							String newsLine = news.get(i);
							Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + (i == (news.size()-1) ? "\\_/ " : (newsLine.startsWith("#") ? " + " : " |  ")) + ChatColor.RESET + (newsLine.startsWith("#") ? ChatColor.YELLOW + newsLine.replaceFirst("#", "") : newsLine));
						}
						getLogger().info("Please stay updated at https://org.spigot.mc/ressources/UltimateSheepWars");
					} else {
						getLogger().info("Plugin is up-to-date.");
					}
				} catch (IOException ex) {
					if (!(ex instanceof FileNotFoundException)) {
						new ExceptionManager(ex).register(false);
						disablePlugin(Level.SEVERE, "You don't have a valid internet connection, please connect to the internet for the plugin to work.");
					}
				}
			}
		}.runTaskAsynchronously(this);
		new DataRegister(this, LOCALHOST, true);
	}
	
	@Override
	public void onDisable() {
		if (this.MySQL_ENABLE)
			try {
				this.DATABASE.closeConnection();
			} catch (SQLException e) {
				new ExceptionManager(e).register(true);
			}
		if (this.ENABLE_PLUGIN) {
			this.save();
			getVersionManager().getCustomEntities().unregisterEntities();
		}
	}
	
	public void disablePlugin(Level level, String reason)
	{
		this.ENABLE_PLUGIN = false;
		this.getLogger().log(level, reason);
		this.getPluginLoader().disablePlugin(this);
	}
	
	private void register(@SuppressWarnings("unchecked") final Class<? extends UltimateSheepWarsEventListener>... classes) {
		try {
			for (final Class<? extends UltimateSheepWarsEventListener> clazz : classes) {
				final Constructor<? extends UltimateSheepWarsEventListener> constructor = clazz.getConstructor(UltimateSheepWarsPlugin.class);
				Bukkit.getPluginManager().registerEvents((Listener) constructor.newInstance(this), (Plugin) this);
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			new ExceptionManager(ex).register(true);
			try {
				throw ex;
			} catch (java.lang.Exception exx) {
				new ExceptionManager(exx).register(true);
			}
		}
	}
	
	private void save() {
    	this.SETTINGS_CONFIG.set("lobby", Utils.toString(ConfigManager.getLocation(Field.LOBBY)));
        for (int i = 0; i < ConfigManager.getLocations(Field.BOOSTERS).size(); ++i) {
        	this.SETTINGS_CONFIG.set("boosters." + i, Utils.toString(ConfigManager.getLocations(Field.BOOSTERS).get(i)));
        }
        for (TeamManager team : TeamManager.values())
          for (int i = 0; i < team.getSpawns().size(); i++)
        	  this.SETTINGS_CONFIG.set("teams." + team.getName() + ".spawns." + i, Utils.toString(team.getSpawns().get(i)));
        try {
			this.SETTINGS_CONFIG.save(this.SETTINGS_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public void load()
	{
        this.saveDefaultConfig();
        this.initClasses();
        if (ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP))
        {
        	ConfigManager.setBoolean(Field.ENABLE_KIT_PERMISSIONS, true);
        	if (VAULT_INSTALLED)
        	{
        		if (!setupEconomy()) {
        			getLogger().info("KIT: You have enable ingame-shop but there is no economy plugin. This is conflicting :(");
        			ConfigManager.setBoolean(Field.ENABLE_KIT_PERMISSIONS, false);
        		} else if (!setupPermissions()) {
        			getLogger().info("KIT: You have enable ingame-shop but there is no permission plugin. This is conflicting :(");
        			ConfigManager.setBoolean(Field.ENABLE_KIT_PERMISSIONS, false);
        		}
        	} else {
        		getLogger().info("KIT: You have enable ingame-shop but vault isn't installed. This is conflicting :(");
        		ConfigManager.setBoolean(Field.ENABLE_KIT_PERMISSIONS, false);
        	}
        }
        if (ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS))
        {
        	if (!MySQL_ENABLE)
        	{
        		getLogger().info("KIT: You have enable required-wins but no database was detected. This is conflicting :(");
        		ConfigManager.setBoolean(Field.ENABLE_KIT_REQUIRED_WINS, false);
        	}
        }
        if (VAULT_INSTALLED) {
        	this.CHAT_PROVIDER_INSTALLED = setupChat();
        } else {
        	this.CHAT_PROVIDER_INSTALLED = false;
        }
        if (this.LEADER_HEADS_INSTALLED && this.MySQL_ENABLE)
        {
        	String[] list = {"Deaths", "Games", "KDRatio", "Kills", "SheepKilled", "SheepThrown", "TotalTime", "WinRate", "Wins"};
        	try {
        		for (String strng : list)
        			loadLeaderHeadsModule("UltimateSheepWars" + strng);
			} catch (ReflectiveOperationException ex) {
				new ExceptionManager(ex).register(true);
			}
        }
        SETTINGS_FILE = new File(getDataFolder(), "settings.yml");
        if (!SETTINGS_FILE.exists())
        {
        	try {
        		SETTINGS_FILE.createNewFile();
			} catch (IOException ex) {
				new ExceptionManager(ex).register(true);
			}
        }
        this.SETTINGS_CONFIG = YamlConfiguration.loadConfiguration(this.SETTINGS_FILE);
        this.WORLD.setSpawnLocation(ConfigManager.getLocation(Field.LOBBY).getBlockX(), ConfigManager.getLocation(Field.LOBBY).getBlockY(), ConfigManager.getLocation(Field.LOBBY).getBlockZ());
        this.WORLD.setKeepSpawnInMemory(true);//Peut faire freeze le load
        this.WORLD.getSpawnLocation().getChunk().load(true);
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        if (ConfigManager.getString(Field.SCOREBOARD_DECORATION).length() <= 21)
    	{
    		new ScoreboardTask(ConfigManager.getString(Field.SCOREBOARD_DECORATION), this);
    	} else {
    		getLogger().warning("[!] The scoreboard-decoration's length is higher than maximum allowed (" + ConfigManager.getString(Field.SCOREBOARD_DECORATION).length() + " > 21)");
    	}
        TeamManager.RED.updateScoreboardTeamCount();
        TeamManager.BLUE.updateScoreboardTeamCount();

        Calendar cal = Calendar.getInstance();
       	this.CHRISTMAS_MODE = (Integer.valueOf(new SimpleDateFormat("MM").format(cal.getTime())) == 12);
       	this.APRIL_FOOL_MODE = (new SimpleDateFormat("dd-MM").format(cal.getTime()).equals("01-04"));
	}
	
	/**
	 * ConfigurationManager
	 * @param initClasses
	 */
	private void initClasses()
	{
		ConfigManager.initConfig(this);
		Message.initMessages();
		Language.loadStartupConfiguration(this);
		
		try {
			File boosterFile = new File(this.getDataFolder(), "boosters.yml");
			if (!boosterFile.exists())
				boosterFile.createNewFile();
			BoosterManager.setupConfig(boosterFile);

			File kitFile = new File(this.getDataFolder(), "kits.yml");
			if (!kitFile.exists())
				kitFile.createNewFile();
			KitManager.setupConfig(kitFile);

			File sheepFile = new File(this.getDataFolder(), "sheeps.yml");
			if (!sheepFile.exists())
				sheepFile.createNewFile();
			SheepManager.setupConfig(sheepFile);
		} catch (IOException ex) {
			new ExceptionManager(ex).register(true);
			disablePlugin(Level.WARNING, "Something prevent the plugin to create important configuration files.");
		}
		
		this.rewardManager = new RewardsManager(this);
        GameState.initGameStates(this);
	}
	
	private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
        	PERMISSION_PROVIDER = permissionProvider.getProvider();
        }
        return (PERMISSION_PROVIDER != null);
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
        	ECONOMY_PROVIDER = economyProvider.getProvider();
        }

        return (ECONOMY_PROVIDER != null);
    }
    
    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
        	CHAT_PROVIDER = chatProvider.getProvider();
        }

        return (CHAT_PROVIDER != null);
    }
	
	@SuppressWarnings("unchecked")
	private <T> T loadLeaderHeadsModule(String name) throws ReflectiveOperationException {
        return (T) ReflectionUtils.instantiateObject(Class.forName(UltimateSheepWarsPlugin.PACKAGE + ".leaderheads." + name));
    }
	
	public static Boolean isSpigotServer(){
        try{
            Class.forName("org.bukkit.entity.Player$Spigot");
            return true;
        }catch(Exception e){
            return false;
        }
    }
	
	public void givePermission(Player player, String permission) {
		try {
			PERMISSION_PROVIDER.playerAdd(player, permission);
		} catch (NoClassDefFoundError | NullPointerException e) {
		}
	}
	
    public void stop()
    {
    	GameState.setCurrentStep(GameState.TERMINATED);
    	String commd = ConfigManager.getString(Field.DISPATCH_COMMAND);
    	if (!commd.equals("stop") && !commd.equals("restart")) {
    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commd);
    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
    	} else {
    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commd);
    	}
    }
    
	public File getPluginJar() {
		return this.getFile();
	}
	
	public static VersionManager getVersionManager() {
		return versionManager;
	}
	
	public BeginCountdown getPreGameTask() {
		return this.PRE_GAME_TASK;
	}
	
	public GameTask getGameTask() {
		return this.GAME_TASK;
	}
	
	public void setPreGameTask(BeginCountdown task) {
		this.PRE_GAME_TASK = task;
	}
	
	public void setGameTask(GameTask task) {
		this.GAME_TASK = task;
	}
}